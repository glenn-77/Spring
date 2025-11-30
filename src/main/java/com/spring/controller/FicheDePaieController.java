package com.spring.controller;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.spring.model.*;
import com.spring.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/fiches")
public class FicheDePaieController {

    private final FicheDePaieService ficheDePaieService;
    private final EmployeService employeService;

    @GetMapping
    public String listFiches(@RequestParam(value = "employeId", required = false) Long employeId,
                             @RequestParam(value = "dateDebut", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                             @RequestParam(value = "dateFin", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                             Model model, HttpSession session) {

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        Employe emp = u.getEmploye();

        List<FicheDePaie> fiches;

        if ("ADMINISTRATEUR".equals(role)) {

            // Base : toutes les fiches (avec employé déjà fetch)
            fiches = ficheDePaieService.getAllWithDetails();

            // Filtre par employé (si choisi)
            if (employeId != null) {
                fiches = fiches.stream()
                        .filter(f -> f.getEmploye() != null
                                && f.getEmploye().getId().equals(employeId))
                        .toList();
            }

            // Filtre par date début
            if (dateDebut != null) {
                fiches = fiches.stream()
                        .filter(f -> f.getDateGeneration() != null
                                && !f.getDateGeneration().isBefore(dateDebut))
                        .toList();
            }

            // Filtre par date fin
            if (dateFin != null) {
                fiches = fiches.stream()
                        .filter(f -> f.getDateGeneration() != null
                                && !f.getDateGeneration().isAfter(dateFin))
                        .toList();
            }

        } else {
            // Utilisateur normal → ne voit QUE ses propres fiches
            fiches = ficheDePaieService.getAllWithDetails().stream()
                    .filter(f -> f.getEmploye() != null
                            && f.getEmploye().getId().equals(emp.getId()))
                    .toList();

            model.addAttribute("fiches", fiches);
        }

        model.addAttribute("fiches", fiches);
        model.addAttribute("employes", employeService.getAll());
        model.addAttribute("employeId", employeId);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);

        return "fiches"; // fiches.html
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("fiche", new FicheDePaie());
        model.addAttribute("employes", employeService.getAll());
        return "fiches-form"; // fiches-form.html
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,  HttpSession session) {
        FicheDePaie f = ficheDePaieService.getByIdWithDetails(id);
        if (f == null) return "redirect:/fiches";

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        Employe emp = u.getEmploye();

        if (!ficheDePaieService.canAccessFiche(f, emp, role)) {
            return "redirect:/fiches?denied=true";
        }

        model.addAttribute("fiche", f);
        model.addAttribute("employes", employeService.getAll());
        return "fiches-form";
    }

    @PostMapping("/save")
    public String saveFiche(@RequestParam(value = "id", required = false) Long id,
                            @RequestParam Long employeId,
                            @RequestParam int mois,
                            @RequestParam int annee,
                            @RequestParam Double salaireBase,
                            @RequestParam(required = false, defaultValue = "0") Double prime,
                            @RequestParam(required = false, defaultValue = "0") Double deduction) {

        FicheDePaie data = new FicheDePaie();
        data.setMois(mois);
        data.setAnnee(annee);
        data.setSalaireBase(salaireBase);
        data.setPrime(prime);
        data.setDeduction(deduction);

        if (id == null) {
            ficheDePaieService.create(data, employeId);
        } else {
            data.setId(id);
            ficheDePaieService.update(id, data);
        }

        return "redirect:/fiches";
    }

    @GetMapping("/delete/{id}")
    public String deleteFiche(@PathVariable Long id, HttpSession session) {
        FicheDePaie f = ficheDePaieService.getByIdWithDetails(id);

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        Employe emp = u.getEmploye();
        if (!ficheDePaieService.canAccessFiche(f, emp, role)) {
            return "redirect:/fiches?denied=true";
        }
        ficheDePaieService.delete(id);
        return "redirect:/fiches";
    }

    @GetMapping("/pdf/{id}")
    public void exportPdf(@PathVariable Long id, HttpServletResponse response) throws Exception {

        FicheDePaie fiche = ficheDePaieService.getById(id);
        if (fiche == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fiche non trouvée");
            return;
        }

        // Nom du fichier de sortie
        String fileName = "fiche_paie_" + fiche.getEmploye().getPrenom() + fiche.getEmploye().getNom() + fiche.getAnnee() + "." + fiche.getAnnee() + ".pdf";

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        // ------ PDF GENERATION ------
        com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(pdf, response.getOutputStream());

        pdf.open();

        // Titre
        pdf.add(new com.itextpdf.text.Paragraph(
                "FICHE DE PAIE",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD)
        ));
        pdf.add(new com.itextpdf.text.Paragraph(" "));

        // Infos employé
        pdf.add(new com.itextpdf.text.Paragraph("Nom : " +
                fiche.getEmploye().getNom()));
        pdf.add(new com.itextpdf.text.Paragraph("Prénom : " + fiche.getEmploye().getPrenom()));
        pdf.add(new com.itextpdf.text.Paragraph("Poste : " + fiche.getEmploye().getPoste()));
        pdf.add(new com.itextpdf.text.Paragraph("Date d'embauche : " + fiche.getEmploye().getDateEmbauche()));
        pdf.add(new com.itextpdf.text.Paragraph("Date de génération : " + fiche.getDateGeneration()));
        pdf.add(new com.itextpdf.text.Paragraph(" "));

        // Tableau des montants
        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell("Salaire Base");
        table.addCell(fiche.getSalaireBase().toString() + "€");

        table.addCell("Prime");
        table.addCell(fiche.getPrime().toString() + "€");

        table.addCell("Déduction");
        table.addCell(fiche.getDeduction().toString() + "€");

        table.addCell("Net à payer");
        table.addCell(fiche.getNetAPayer().toString() + "€");

        pdf.add(table);
        pdf.add(new com.itextpdf.text.Paragraph("\n"));

        Paragraph footer = new Paragraph("Document généré automatiquement — " + LocalDate.now(),
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        footer.setAlignment(Element.ALIGN_CENTER);
        pdf.add(footer);
        pdf.close();
    }

}

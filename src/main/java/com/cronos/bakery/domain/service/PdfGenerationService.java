package com.cronos.bakery.domain.service;

import com.cronos.bakery.application.dto.recipes.RecipeCostCalculation;
import com.cronos.bakery.domain.entity.core.Allergen;
import com.cronos.bakery.domain.entity.core.User;
import com.cronos.bakery.domain.entity.quote.Quote;
import com.cronos.bakery.domain.entity.recipes.Recipe;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

    private final TemplateEngine templateEngine;

    /**
     * Generates a quote PDF
     */
    public byte[] generateQuotePdf(Quote quote) {
        try {
            User user = quote.getUser();
            Locale locale = getLocale(user.getDefaultLanguage());

            Context context = new Context(locale);
            context.setVariable("quote", quote);
            context.setVariable("user", user);
            context.setVariable("currencyFormat", getCurrencyFormat(locale, quote.getCurrency()));
            context.setVariable("dateFormat", DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String htmlContent = templateEngine.process("pdf/quote-template", context);

            return convertHtmlToPdf(htmlContent);

        } catch (Exception e) {
            log.error("Error generating quote PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    /**
     * Generates a recipe cost breakdown PDF
     */
    public byte[] generateRecipeCostPdf(Recipe recipe, RecipeCostCalculation costCalc, User user) {
        try {
            Locale locale = getLocale(user.getDefaultLanguage());

            Context context = new Context(locale);
            context.setVariable("recipe", recipe);
            context.setVariable("costCalc", costCalc);
            context.setVariable("user", user);
            context.setVariable("currencyFormat", getCurrencyFormat(locale, user.getDefaultCurrency()));

            String htmlContent = templateEngine.process("pdf/recipe-cost-template", context);

            return convertHtmlToPdf(htmlContent);

        } catch (Exception e) {
            log.error("Error generating recipe cost PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    /**
     * Generates nutritional label PDF
     */
    public byte[] generateNutritionalLabelPdf(Recipe recipe, NutritionalInfo nutritionalInfo, User user) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph(recipe.getName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add nutritional information
            addNutritionalTable(document, nutritionalInfo);

            // Add allergen information
            if (!recipe.getAllergens().isEmpty()) {
                addAllergenSection(document, recipe, user.getDefaultLanguage());
            }

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error generating nutritional label PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private byte[] convertHtmlToPdf(String html) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }

    private void addNutritionalTable(Document document, NutritionalInfo info) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

        // Header
        PdfPCell headerCell = new PdfPCell(new Phrase("Información Nutricional", headerFont));
        headerCell.setColspan(2);
        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        headerCell.setPadding(8);
        table.addCell(headerCell);

        // Add rows
        addNutritionalRow(table, "Calorías", info.getCalories() + " kcal", cellFont);
        addNutritionalRow(table, "Proteínas", info.getProteins() + " g", cellFont);
        addNutritionalRow(table, "Carbohidratos", info.getCarbohydrates() + " g", cellFont);
        addNutritionalRow(table, "Grasas", info.getFats() + " g", cellFont);
        addNutritionalRow(table, "Fibra", info.getFiber() + " g", cellFont);
        addNutritionalRow(table, "Azúcares", info.getSugars() + " g", cellFont);
        addNutritionalRow(table, "Sodio", info.getSodium() + " mg", cellFont);

        document.add(table);
    }

    private void addNutritionalRow(PdfPTable table, String label, String value, Font font) {
        table.addCell(new Phrase(label, font));
        table.addCell(new Phrase(value, font));
    }

    private void addAllergenSection(Document document, Recipe recipe, String language) throws DocumentException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font contentFont = new Font(Font.FontFamily.HELVETICA, 11);

        Paragraph allergenHeader = new Paragraph(
                "es".equals(language) ? "Alérgenos" : "Allergens",
                headerFont
        );
        allergenHeader.setSpacingBefore(20);
        allergenHeader.setSpacingAfter(10);
        document.add(allergenHeader);

        List allergenList = new List(List.UNORDERED);
        for (Allergen allergen : recipe.getAllergens()) {
            String allergenName = "es".equals(language) ? allergen.getNameEs() : allergen.getNameEn();
            allergenList.add(new ListItem(allergenName, contentFont));
        }

        document.add(allergenList);
    }

    private Locale getLocale(String language) {
        return "es".equals(language) ? Locale.of("es", "MX") : Locale.ENGLISH;
    }

    private NumberFormat getCurrencyFormat(Locale locale, String currency) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        try {
            format.setCurrency(java.util.Currency.getInstance(currency));
        } catch (Exception e) {
            log.warn("Invalid currency code: {}", currency);
        }
        return format;
    }
}

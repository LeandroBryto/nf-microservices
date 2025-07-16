package leandro.dev.nf_service.generator;

import leandro.dev.nf_service.dto.SaleDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Slf4j
public class PdfGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

    public byte[] gererNotaFiscalPdf(SaleDTO  venda, String numeroNf) throws IOException{
        log.info("Gerando PDF da nota fiscal {} para venda ID: {}", numeroNf , venda.getId());

        try (PDDocument document = new PDDocument()){
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document , page)){
                // configurar fonte
                PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

                float yPosition = 750;
                float margin = 50;
                float fontSize = 12;
                float titleFontSize = 16;

                // titulo

                contentStream.beginText();;
                contentStream.setFont(fontBold,titleFontSize);
                contentStream.newLineAtOffset(margin,yPosition);
                contentStream.showText("NOTA FISCAL ELETRÔNICA");
                contentStream.endText();

                yPosition -= 40;

                // numero da NF

                contentStream.beginText();
                contentStream.setFont(fontBold, fontSize);
                contentStream.newLineAtOffset(margin,yPosition);
                contentStream.showText("Número: " + numeroNf);
                contentStream.endText();

                yPosition -= 30;

                // data de emissão
                contentStream.beginText();
                contentStream.setFont(font,fontSize);
                contentStream.newLineAtOffset(margin,yPosition);
                contentStream.showText("Data de Emissão:" + venda.getDataVenda().format(DATE_FORMATTER));
                contentStream.endText();

                yPosition -= 40;

                // Dados do cliente
                contentStream.beginText();
                contentStream.setFont(fontBold, fontSize);
                contentStream.newLineAtOffset(margin,yPosition);
                contentStream.showText("DADOS DO CLIENTE");
                contentStream.endText();

                yPosition -= 25;

                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.newLineAtOffset(margin , yPosition);
                contentStream.showText("Nome: " + venda.getClienteNome());
                contentStream.endText();


                yPosition -= 20;

                contentStream.beginText();
                contentStream.setFont(font,fontSize);
                contentStream.newLineAtOffset(margin,yPosition);
                contentStream.showText("CPF: " + venda.getClienteCpf());
                contentStream.endText();

                yPosition -= 20;

                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Email: " + venda.getClienteEmail());
                contentStream.endText();

                yPosition -= 40;

                // dados Prodduto/serviço
                contentStream.beginText();
                contentStream.setFont(fontBold,fontSize);
                contentStream.newLineAtOffset(margin,yPosition);
                contentStream.showText("DESCRIÇÃO DO PRODUTO/SERVIÇO");
                contentStream.endText();

                yPosition -=25;


                String descricao = venda.getDescricao() != null ? venda.getDescricao() : "Produto/Serviço";
                contentStream.beginText();
                contentStream.setFont(font,fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Descrição" + descricao);
                contentStream.endText();

                yPosition -= 20;

                contentStream.beginText();
                contentStream.setFont(font,fontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Quantidade: 1");
                contentStream.endText();

                yPosition -= 20;

                contentStream.beginText();
                contentStream.setFont(font,fontSize);
                contentStream.newLineAtOffset(margin,yPosition);
                contentStream.showText("Valor Unitário: " + CURRENCY_FORMATTER.format(venda.getValor()));
                contentStream.endText();

                yPosition -= 40;

                // Total
                contentStream.beginText();
                contentStream.setFont(font,titleFontSize);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("VALOR TOTAL: " + CURRENCY_FORMATTER.format(venda.getValor()));
                contentStream.endText();

                yPosition -=60;

                // Informação adcionais

                contentStream.beginText();
                contentStream.setFont(font, 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Esta é uma nota fiscal simulada para fins de demostração.");
                contentStream.endText();

                yPosition -= 15;

                contentStream.beginText();
                contentStream.setFont(font, 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Gerada automaticamente pela Sistema Nota Fiscal");
                contentStream.endText();

                yPosition -= 15;


                contentStream.beginText();
                contentStream.setFont(font, 10);
                contentStream.newLineAtOffset(margin,yPosition);
                contentStream.showText("ID da Venda: " + venda.getId());
                contentStream.endText();

                yPosition -= 15;

                contentStream.beginText();
                contentStream.setFont(font , 10);
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("ID do Pedido: " + venda.getOrderId());
                contentStream.endText();

            }
            // converter para byte arry
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            byte[] pdfBytes = baos.toByteArray();

            log.info("PDF da nota fiscal {} gerado com sucesso. Tamanho: {} bytes", numeroNf, pdfBytes.length);
            return pdfBytes;
        }
    }
}

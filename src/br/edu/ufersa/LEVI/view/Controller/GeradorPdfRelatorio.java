package br.edu.ufersa.LEVI.view.Controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Gera o PDF do relatório de aluguéis (geral ou de um cliente específico)
// usando Apache PDFBox. Esta classe só monta o documento; quem decide o que
// vai dentro dele (RelatorioController) passa os dados já prontos.
public class GeradorPdfRelatorio {

    private static final float MARGEM = 50f;
    private static final float ALTURA_LINHA = 16f;

    // Gera o PDF e salva na pasta Downloads do usuário.
    // Retorna o arquivo gerado, para o controller poder informar o caminho.
    public File gerar(String tituloRelatorio, String subtitulo, List<LinhaRelatorio> linhas,
                       float faturamentoPeriodo) throws IOException {

        try (PDDocument documento = new PDDocument()) {
            // A4 em modo paisagem: mais largura disponível para as 6 colunas da tabela
            PDRectangle a4Paisagem = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
            PDPage pagina = new PDPage(a4Paisagem);
            documento.addPage(pagina);

            PDType1Font fonteTitulo = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fonteTexto = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fonteCabecalhoTabela = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            float larguraUtil = pagina.getMediaBox().getWidth() - 2 * MARGEM;
            float y = pagina.getMediaBox().getHeight() - MARGEM;

            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {

                // Cabeçalho
                conteudo.beginText();
                conteudo.setFont(fonteTitulo, 20);
                conteudo.newLineAtOffset(MARGEM, y);
                conteudo.showText("DUDUTECA");
                conteudo.endText();
                y -= 26;

                conteudo.beginText();
                conteudo.setFont(fonteTitulo, 14);
                conteudo.newLineAtOffset(MARGEM, y);
                conteudo.showText(tituloRelatorio);
                conteudo.endText();
                y -= 18;

                if (subtitulo != null && !subtitulo.isBlank()) {
                    conteudo.beginText();
                    conteudo.setFont(fonteTexto, 11);
                    conteudo.newLineAtOffset(MARGEM, y);
                    conteudo.showText(subtitulo);
                    conteudo.endText();
                    y -= 16;
                }

                String dataGeracao = "Gerado em " + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                conteudo.beginText();
                conteudo.setFont(fonteTexto, 9);
                conteudo.newLineAtOffset(MARGEM, y);
                conteudo.showText(dataGeracao);
                conteudo.endText();
                y -= 24;

                // Linha divisória
                conteudo.setLineWidth(1f);
                conteudo.moveTo(MARGEM, y);
                conteudo.lineTo(MARGEM + larguraUtil, y);
                conteudo.stroke();
                y -= 20;

                // Cabeçalho da tabela
                // Larguras pensadas para o conteúdo real de cada coluna:
                // "Livro/Disco" tem textos longos (título + autor + gênero),
                // "Status" e "Valor" são sempre curtos.
                float[] colunas = {0f, 0.20f, 0.62f, 0.74f, 0.86f, 0.93f};
                String[] titulosColunas = {"Cliente", "Livro/Disco", "Início", "Devolução", "Status", "Valor"};

                conteudo.setFont(fonteCabecalhoTabela, 9);
                conteudo.beginText();
                conteudo.newLineAtOffset(MARGEM, y);
                conteudo.showText(titulosColunas[0]);
                conteudo.endText();
                for (int i = 1; i < titulosColunas.length; i++) {
                    conteudo.beginText();
                    conteudo.newLineAtOffset(MARGEM + larguraUtil * colunas[i], y);
                    conteudo.showText(titulosColunas[i]);
                    conteudo.endText();
                }
                y -= 6;
                conteudo.setLineWidth(0.5f);
                conteudo.moveTo(MARGEM, y);
                conteudo.lineTo(MARGEM + larguraUtil, y);
                conteudo.stroke();
                y -= ALTURA_LINHA;

                // Linhas da tabela
                conteudo.setFont(fonteTexto, 9);
                for (LinhaRelatorio linha : linhas) {
                    if (y < MARGEM + 60) {
                        // Sem espaço nesta página; este relatório simples não
                        // pagina automaticamente, então paramos por aqui.
                        break;
                    }

                    // Largura disponível de cada coluna, em pontos, usada para
                    // truncar o texto e ele nunca invadir a coluna seguinte.
                    float larguraColCliente = larguraUtil * (colunas[1] - colunas[0]) - 5;
                    float larguraColItem = larguraUtil * (colunas[2] - colunas[1]) - 5;

                    escreverCelula(conteudo, fonteTexto, truncar(fonteTexto, linha.getCliente(), larguraColCliente, 9),
                            MARGEM, y);
                    escreverCelula(conteudo, fonteTexto, truncar(fonteTexto, linha.getItem(), larguraColItem, 9),
                            MARGEM + larguraUtil * colunas[1], y);
                    escreverCelula(conteudo, fonteTexto, linha.getDataInicioFormatada(),
                            MARGEM + larguraUtil * colunas[2], y);
                    escreverCelula(conteudo, fonteTexto, linha.getDataFimFormatada(),
                            MARGEM + larguraUtil * colunas[3], y);
                    escreverCelula(conteudo, fonteTexto, linha.getStatus(),
                            MARGEM + larguraUtil * colunas[4], y);
                    escreverCelula(conteudo, fonteTexto, linha.getValorFormatado(),
                            MARGEM + larguraUtil * colunas[5], y);

                    y -= ALTURA_LINHA;
                }

                if (linhas.isEmpty()) {
                    conteudo.beginText();
                    conteudo.setFont(fonteTexto, 10);
                    conteudo.newLineAtOffset(MARGEM, y);
                    conteudo.showText("Nenhum aluguel encontrado para este filtro.");
                    conteudo.endText();
                    y -= ALTURA_LINHA;
                }

                y -= 10;
                conteudo.setLineWidth(1f);
                conteudo.moveTo(MARGEM, y);
                conteudo.lineTo(MARGEM + larguraUtil, y);
                conteudo.stroke();
                y -= 20;

                conteudo.beginText();
                conteudo.setFont(fonteTitulo, 12);
                conteudo.newLineAtOffset(MARGEM, y);
                conteudo.showText(String.format("Faturamento do período: R$ %.2f", faturamentoPeriodo));
                conteudo.endText();
            }

            File pastaDownloads = obterPastaDownloads();
            String nomeArquivo = "relatorio_duduteca_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            File arquivoFinal = new File(pastaDownloads, nomeArquivo);

            documento.save(arquivoFinal);
            return arquivoFinal;
        }
    }

    private void escreverCelula(PDPageContentStream conteudo, PDType1Font fonte, String texto, float x, float y) throws IOException {
        if (texto == null) texto = "-";
        conteudo.beginText();
        conteudo.setFont(fonte, 9);
        conteudo.newLineAtOffset(x, y);
        conteudo.showText(texto);
        conteudo.endText();
    }

    // Corta o texto (terminando em "...") se ele for mais largo do que o
    // espaço disponível na coluna, medindo a largura real com a fonte usada.
    // Sem isso, textos longos como a descrição de um Produto invadem a
    // coluna seguinte da tabela.
    private String truncar(PDType1Font fonte, String texto, float larguraMaximaPontos, float tamanhoFonte) throws IOException {
        if (texto == null) return "-";

        float largura = fonte.getStringWidth(texto) / 1000 * tamanhoFonte;
        if (largura <= larguraMaximaPontos) {
            return texto;
        }

        String reticencias = "...";
        float larguraReticencias = fonte.getStringWidth(reticencias) / 1000 * tamanhoFonte;

        StringBuilder cortado = new StringBuilder();
        float larguraAtual = 0;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            float larguraChar = fonte.getStringWidth(String.valueOf(c)) / 1000 * tamanhoFonte;
            if (larguraAtual + larguraChar + larguraReticencias > larguraMaximaPontos) {
                break;
            }
            cortado.append(c);
            larguraAtual += larguraChar;
        }
        return cortado + reticencias;
    }

    // Pasta Downloads do usuário, igual em qualquer sistema (Windows/Linux/Mac)
    private File obterPastaDownloads() {
        Path downloads = Path.of(System.getProperty("user.home"), "Downloads");
        File pasta = downloads.toFile();
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
        return pasta;
    }
}

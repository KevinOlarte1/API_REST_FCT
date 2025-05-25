package com.kevinolarte.resibenissa.services.moduloWallet;

import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.moduloWallet.MovimientoWallet;
import com.kevinolarte.resibenissa.models.moduloWallet.Wallet;
import com.kevinolarte.resibenissa.repositories.moduloWallet.MovimientoWalletRepository;
import com.kevinolarte.resibenissa.repositories.moduloWallet.WalletRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;




@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final MovimientoWalletRepository movimientoWalletRepository;
    //DEPOSIT -- AÑADIR

    //EXPENSE -- RETIRAR

    //GET -- OBTENER SALDO

    //Informe
    public byte[] getInforme(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResiException(ApiErrorCode.WALLET_NO_ENCONTRADA));

        List<MovimientoWallet> movimientos = movimientoWalletRepository.findByWallet(wallet);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        document.add(new Paragraph("INFORME DE MOVIMIENTOS"));
        document.add(new Paragraph("Residente: " + wallet.getResidente().getNombre()));
        document.add(new Paragraph("Saldo actual: " + wallet.getSaldoTotal()));
        document.add(new Paragraph(" ")); // Espacio

        PdfPTable table = new PdfPTable(4);
        table.addCell("Fecha");
        table.addCell("Tipo");
        table.addCell("Cantidad");
        table.addCell("Concepto");

        for (MovimientoWallet m : movimientos) {
            table.addCell(m.getFecha().toString());
            table.addCell(m.getTipo().toString());
            table.addCell(String.format("%.2f", m.getCantidad()));
            table.addCell(m.getConcepto());
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }



}

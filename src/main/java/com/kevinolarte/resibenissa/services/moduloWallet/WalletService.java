package com.kevinolarte.resibenissa.services.moduloWallet;

import com.kevinolarte.resibenissa.dto.in.modeloWallet.MovimientoRequestDTO;
import com.kevinolarte.resibenissa.enums.moduloWallet.TipoMovimiento;
import com.kevinolarte.resibenissa.exceptions.ApiErrorCode;
import com.kevinolarte.resibenissa.exceptions.ResiException;
import com.kevinolarte.resibenissa.models.Residente;
import com.kevinolarte.resibenissa.models.moduloWallet.MovimientoWallet;
import com.kevinolarte.resibenissa.models.moduloWallet.Wallet;
import com.kevinolarte.resibenissa.repositories.moduloWallet.MovimientoWalletRepository;
import com.kevinolarte.resibenissa.repositories.moduloWallet.WalletRepository;
import com.kevinolarte.resibenissa.services.ResidenteService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;




@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final MovimientoWalletRepository movimientoWalletRepository;
    private final ResidenteService residenteService;

    /**
     * Genera un informe PDF de los movimientos de la wallet de un residente.
     *
     * @param idResidencia ID de la residencia del residente.
     * @param idResidente  ID del residente del cual se genera el informe.
     * @return Un array de bytes que representa el PDF del informe.
     * @throws ResiException si no se encuentra la wallet o si ocurre un error al generar el PDF.
     */
    public byte[] getInforme(Long idResidencia, Long idResidente) throws ResiException, DocumentException {

        Residente residente = residenteService.getResidente(idResidencia, idResidente);
        Wallet wallet = walletRepository.findById(residente.getWallet().getId()).orElse(null);
        if (wallet == null) {
            throw new ResiException(ApiErrorCode.WALLET_NO_ENCONTRADA);
        }
        List<MovimientoWallet> movimientos = movimientoWalletRepository.findByWallet(wallet);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        document.add(new Paragraph("INFORME DE MOVIMIENTOS"));
        document.add(new Paragraph("Residente: " + wallet.getResidente().getNombre()));
        document.add(new Paragraph("Saldo actual: " + String.format("%.2f", wallet.getSaldoTotal())));
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


    /**
     * Deposita una cantidad en la wallet de un residente.
     *
     * @param idResidencia ID de la residencia del residente.
     * @param idResidente  ID del residente al que se le deposita el dinero.
     * @param input         DTO que contiene la cantidad a depositar.
     * @throws ResiException si el monto es inválido o si la wallet no se encuentra.
     */
    public void deposit(Long idResidencia, Long idResidente, MovimientoRequestDTO input) {
        if (moneyValid(input.getCantidad())) {
            throw new ResiException(ApiErrorCode.MONTO_INVALIDO); // crea este código si aún no lo tienes
        }
        System.out.println("asdaaasdasdasd");
        Residente residente = residenteService.getResidente(idResidencia, idResidente);
        Wallet wallet = walletRepository.findById(residente.getWallet().getId()).orElseThrow(() ->
                new ResiException(ApiErrorCode.WALLET_NO_ENCONTRADA));

        System.out.println("Wallet encontrada: " + wallet.getId() + ", Saldo actual: " + wallet.getSaldoTotal());
        System.out.println("Cantidad a depositar: " + input.getCantidad());
        // Crear movimiento
        MovimientoWallet movimiento = new MovimientoWallet();
        movimiento.setWallet(wallet);
        movimiento.setCantidad(input.getCantidad());
        movimiento.setTipo(TipoMovimiento.IN);
        movimiento.setConcepto(input.getConcepto());
        movimiento.setFecha(LocalDateTime.now());

        // Actualizar saldo
        Double nuevoSaldo = wallet.getSaldoTotal() + input.getCantidad();
        wallet.setSaldoTotal(nuevoSaldo);

        // Guardar movimiento y wallet
        movimientoWalletRepository.save(movimiento);
        walletRepository.save(wallet);
    }

    /**
     * Metodo para verificar si la cantidad es valida
     * @param cantidad dinero que se va comprobar
     * @return booleano comprobando si es valido esa cantidad de dinero.
     */
    public boolean moneyValid(double cantidad) {
        if (cantidad <= 0) return false;

        // Multiplicamos por 100 y comprobamos si tiene más de 2 decimales
        double scaled = cantidad * 100;
        return scaled == Math.floor(scaled);
    }


    public void retire(Long idResidencia, Long idResidente, MovimientoRequestDTO input) {
        if (moneyValid(input.getCantidad())) {
            throw new ResiException(ApiErrorCode.MONTO_INVALIDO);
        }

        Residente residente = residenteService.getResidente(idResidencia, idResidente);
        Wallet wallet = walletRepository.findById(residente.getWallet().getId())
                .orElseThrow(() -> new ResiException(ApiErrorCode.WALLET_NO_ENCONTRADA));

        if (wallet.getSaldoTotal() < input.getCantidad()) {
            throw new ResiException(ApiErrorCode.SALDO_INSUFICIENTE);
        }

        MovimientoWallet movimiento = new MovimientoWallet();
        movimiento.setWallet(wallet);
        movimiento.setCantidad(input.getCantidad());
        movimiento.setTipo(TipoMovimiento.OUT);
        movimiento.setConcepto(input.getConcepto() != null && !input.getConcepto().trim().isEmpty() ? input.getConcepto() : "Retiro manual");
        movimiento.setFecha(LocalDateTime.now());

        wallet.setSaldoTotal(wallet.getSaldoTotal() - input.getCantidad());

        movimientoWalletRepository.save(movimiento);
        walletRepository.save(wallet);
    }

    public Double getSaldo(Long idResidencia, Long idResidente) {
        Residente residente = residenteService.getResidente(idResidencia, idResidente);
        Wallet wallet = walletRepository.findById(residente.getWallet().getId())
                .orElseThrow(() -> new ResiException(ApiErrorCode.WALLET_NO_ENCONTRADA));

        return wallet.getSaldoTotal();
    }
}

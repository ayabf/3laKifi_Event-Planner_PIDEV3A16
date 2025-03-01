package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {

    public static void generateQRCodeToFile(String text, String filePath, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        // ✅ Vérifier et créer le dossier `qrcodes/` si nécessaire
        File qrCodeDirectory = new File("qrcodes");
        if (!qrCodeDirectory.exists()) {
            System.out.println("📁 Création du dossier qrcodes...");
            boolean dirCreated = qrCodeDirectory.mkdir();
            if (!dirCreated) {
                System.err.println("❌ Impossible de créer le dossier qrcodes !");
                return;
            }
        }

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        System.out.println("✅ QR Code enregistré dans : " + filePath);
    }
}

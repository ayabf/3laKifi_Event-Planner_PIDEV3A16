import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import javax.imageio.ImageIO;
import java.io.File;

public class QRCodeReader {
    public static String readQRCode(String filePath) throws Exception {
        File qrFile = new File(filePath);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(ImageIO.read(qrFile))
        ));

        Result result = new MultiFormatReader().decode(binaryBitmap);
        return result.getText();
    }
}

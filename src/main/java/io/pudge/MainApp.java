package io.pudge;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.camel.main.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        int i = 7;
        for(i = 7; i > 0; i --){
            doOCR(i + ".jpg");
        }
    }

    private static String doOCR(String imageFileName) throws IOException, TesseractException {

        System.out.println("OCR image [" + imageFileName + "]");
        String result = null;
        File imageFile = new File("images/" + imageFileName);
        if(imageFile.exists()){
            BufferedImage textImage = ImageIO.read(imageFile);
            int x = 240;
            int y = 25;
            int scale = 3;
            textImage = ImageHelper.getSubImage(textImage, 0, 0, x, y);
            // 这里对图片黑白处理,增强识别率.这里先通过截图,截取图片中需要识别的部分
            textImage = ImageHelper.convertImageToGrayscale(textImage);
            // 图片锐化,自己使用中影响识别率的主要因素是针式打印机字迹不连贯,所以锐化反而降低识别率
            //textImage = ImageHelper.convertImageToBinary(textImage);
            // 图片放大scale倍,增强识别率(很多图片本身无法识别,放大5倍时就可以轻易识)
            textImage = ImageHelper.getScaledInstance(textImage, x * scale, y * scale);

            Tesseract instance = new Tesseract();  // JNA Interface Mapping
            instance.setLanguage("chi_sim");
            result = instance.doOCR(textImage);
        } else {
            System.out.println("file not exist");
        }

        System.out.println(imageFileName + " contains text : " + result);
        return result;
    }


    private static float compareImage(File fileA, File fileB) {

        float percentage = 0;
        try {
            // take buffer data from both image files //
            BufferedImage biA = ImageIO.read(fileA);
            DataBuffer dbA = biA.getData().getDataBuffer();
            int sizeA = dbA.getSize();
            BufferedImage biB = ImageIO.read(fileB);
            DataBuffer dbB = biB.getData().getDataBuffer();
            int sizeB = dbB.getSize();
            int count = 0;
            // compare data-buffer objects //
            if (sizeA == sizeB) {

                for (int i = 0; i < sizeA; i++) {

                    if (dbA.getElem(i) == dbB.getElem(i)) {
                        count = count + 1;
                    }

                }
                percentage = (count * 100) / sizeA;
            } else {
                System.out.println("Both the images are not of same size");
            }

        } catch (Exception e) {
            System.out.println("Failed to compare image files ...");
        }
        return percentage;
    }

}


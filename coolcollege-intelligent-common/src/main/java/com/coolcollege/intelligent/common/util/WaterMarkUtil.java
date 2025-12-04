package com.coolcollege.intelligent.common.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import com.coolcollege.intelligent.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * 水印
 * 
 * @author yezhe
 * @time 2020/12/03
 */
@Slf4j
public class WaterMarkUtil {

    /**
     *
     * @param is
     *            原图片输入流
     * @param tarImgPath
     *            新图片的路径
     * @param waterMarkContents
     *            水印的内容
     * @param color
     *            水印的颜色
     * @param font
     *            水印的字体
     */
    public static void addWatermark(InputStream is, String tarImgPath, String[] waterMarkContents, Color color,
        Font font) {
        try {
            // 读取原图片信息
            Image srcImg = ImageIO.read(is);
            // 宽、高
            int srcImgWidth = srcImg.getWidth(null);
            int srcImgHeight = srcImg.getHeight(null);
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            // 设置水印颜色
            g.setColor(color);
            g.setFont(font);
            // 抗锯齿
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int fontSize = font.getSize();
            // 相对与X的起始的位置
            int originX = font.getSize();
            // 相对与Y的起始的位置
            int originY = srcImgHeight;
            for (int i = waterMarkContents.length - 1; i >= 0; i--) {
                String waterMarkContent = waterMarkContents[i];
                int fontLength = getWaterMarkLength(waterMarkContent, g);
                // 实际生成的水印文字，实际文字行数
                double textLineCount = Math.ceil(Integer.valueOf(fontLength).doubleValue()
                    / Integer.valueOf(srcImgWidth - fontSize * 2).doubleValue());
                if(textLineCount * fontSize  > originY / 5){
                    int scaleDown = (int) textLineCount / 2;
                    if(scaleDown == Constants.ONE){
                        scaleDown = 2;
                        textLineCount = 1;
                    }else{
                        textLineCount = 2;
                    }
                    fontSize = fontSize / scaleDown;
                    originX = fontSize;
                    font = new Font("微软雅黑", Font.PLAIN, fontSize);
                    g.setFont(font);
                }
                // 实际所有的水印文字的高度
                int textHeight = (int)textLineCount * fontSize;
                originY = originY - textHeight;
                // 文字叠加,自动换行叠加
                int tempY = originY;
                int tempCharLen;// 单字符长度
                int tempLineLen = 0;// 单行字符总长度临时计算
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < waterMarkContent.length(); j++) {
                    char tempChar = waterMarkContent.charAt(j);
                    tempCharLen = getCharLen(tempChar, g);
                    if (tempLineLen >= srcImgWidth - fontSize * 2) {
                        // 绘制前一行
                        g.drawString(stringBuilder.toString(), originX, tempY);
                        // 清空内容,重新追加
                        stringBuilder.delete(0, stringBuilder.length());
                        // 文字长度已经满一行,Y的位置加1字符高度
                        tempY = tempY + fontSize;
                        tempLineLen = 0;
                    }
                    // 追加字符
                    stringBuilder.append(tempChar);
                    tempLineLen += tempCharLen;
                }
                // 最后叠加余下的文字
                g.drawString(stringBuilder.toString(), originX, tempY);

            }

            g.dispose();
            // 输出图片
            FileOutputStream outImgStream = new FileOutputStream(tarImgPath);
            ImageIO.write(bufImg, "png", outImgStream);
            outImgStream.flush();
            outImgStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取水印的坐标
     * 
     * @param waterMarkContent
     *            水印内容
     * @param g
     *            2d图像
     * @return 水印的长度
     */
    public static int getWaterMarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).stringWidth(waterMarkContent);
    }


    public static int getCharLen(char c, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charWidth(c);
    }

    public static void main(String[] args) throws FileNotFoundException {

        Font font = new Font("微软雅黑", Font.PLAIN, 20);

        String srcImgPath = "C:\\Users\\yezhe\\Desktop\\Dingtalk_20201203102146.jpg";
        String tarImgPath = "C:\\Users\\yezhe\\Desktop\\wahaha.jpg";
        String watermarkContent = "地方萨芬\n" + "范德萨\n" + "的萨芬士大夫发射点十分大";
        String[] watermarkContents = new String[] {watermarkContent};
        Color color = new Color(108, 226, 236, 128);

        WaterMarkUtil.addWatermark(new FileInputStream(srcImgPath), tarImgPath, watermarkContents, color, font);

    }
}
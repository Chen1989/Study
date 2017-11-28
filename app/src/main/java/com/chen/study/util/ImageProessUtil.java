package com.chen.study.util;

import android.graphics.Bitmap;
import android.graphics.Color;



/**
 * Created by PengChen on 2017/11/24.
 */

public class ImageProessUtil {
    //马赛克
    public static Bitmap Masic(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];

        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        int index = 0;

        int offsetX = 0, offsetY = 0;
        int newX = 0, newY = 0;
        int size = 10;
        double total = size * size;
        double sumred = 0, sumgreen = 0, sumblue = 0;
        for(int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for(int col = 0; col < width; col++) {
                newY = (row / size) * size;
                newX = (col / size) * size;
                offsetX = newX + size;
                offsetY = newY + size;
                for(int subRow = newY; subRow < offsetY; subRow++) {
                    for(int subCol = newX; subCol < offsetX; subCol++) {
                        if(subRow < 0 || subRow >= height) {
                            continue;
                        }
                        if(subCol < 0 || subCol >= width) {
                            continue;
                        }
                        index = subRow * width + subCol;
                        ta = (inPixels[index] >> 24) & 0xff;
                        sumred += (inPixels[index] >> 16) & 0xff;
                        sumgreen += (inPixels[index] >> 8) & 0xff;
                        sumblue += inPixels[index] & 0xff;
                    }
                }
                index = row * width + col;
                tr = (int)(sumred / total);
                tg = (int)(sumgreen / total);
                tb = (int)(sumblue / total);
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;

                sumred = sumgreen = sumblue = 0; // reset them...
            }
        }
        result.setPixels(outPixels, 0, width, 0, 0, width, height);
        return result;
    }

    //水波纹
    public static Bitmap WaterWave(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] buf1 = new int[w * h];
        int[] buf2 = new int[w * h];
        int[] source = new int[w * h];

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        bitmap.getPixels(source, 0, w, 0, 0, w, h);

        int[] temp = new int[source.length];

        int x = w / 2;
        int y = h / 2;
        int stonesize = Math.max(w, h) / 4;
        int stoneweight = Math.max(w, h);


        if ((x + stonesize) > w || (y + stonesize) > h || (x - stonesize) < 0 || (y - stonesize) < 0){
            return null;
        }
        for (int posx = x - stonesize; posx < x + stonesize; posx++){
            for (int posy = y - stonesize; posy < y + stonesize; posy++){
                if ((posx - x) * (posx - x) + (posy - y) * (posy - y) <= stonesize * stonesize){
                    buf1[w * posy + posx] = (int)-stoneweight;
                }
            }
        }
        for(int i = 0 ; i < 170; i++){
            for (int j = w; j < w * h - w; j++){
                //波能扩散
                buf2[j] =(int)(((buf1[j-1]+buf1[j+1]+buf1[j-w]+buf1[j+w])>>1)- buf2[j]);
                //波能衰减
                buf2[j] -= buf2[j]>>5;
            }
            //交换波能数据缓冲区
            int[] tmp =buf1;
            buf1 = buf2;
            buf2 = tmp;

        /* 渲染水纹效果 */
            int xoff, yoff;
            int k = w;
            for (int m = 1; m < h - 1; m++) {
                for (int j = 0; j < w; j++) {
                    //计算偏移量
                    xoff = buf1[k-1]-buf1[k+1];
                    yoff = buf1[k-w]-buf1[k+w];
                    //判断坐标是否在窗口范围内
                    if ((m+yoff )< 0 || (m+yoff )>= h || (j+xoff )< 0 || (j+xoff )>= w) {
                        k++;
                        continue;
                    }
                    //计算出偏移象素和原始象素的内存地址偏移量
                    int pos1, pos2;
                    pos1=w * (m + yoff) + (j + xoff);
                    pos2=w * m + j;
                    temp[pos2++]=source[pos1++];
                    k++;
                }
            }
        }
        result.setPixels(temp, 0, w, 0, 0, w, h);

        return result;
    }

    //百叶窗
    public static Bitmap Blind(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        //垂直方向上的百叶窗
        boolean _direct = false;//horizontal: true,  vertical: false

        int _width = w / 10; //10个
        int _opacity = 100;
        int _color = 0x000000;

        int r, g, b, a = 0, color;
        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);

        for(int x = 0 ; x < (w - 1) ; x++){
            for(int y = 0 ; y < (h - 1) ; y++){
                color = oldPx[x * h + y];
                r = Color.red(color);
                g = Color.green(color);
                b = Color.blue(color);

                int  nMod = 0 ;
                if (_direct) // 水平方向
                    nMod = y % _width ;
                else if (_direct == false) // 垂直方向
                    nMod = x % _width ;

                double fDelta = 255.0 * (_opacity/100.0) / (_width-1.0);
//                a = Function.FClamp0255(nMod * fDelta) ;
                int colorR = _color & 0xFF0000 >> 16;
                int colorG = _color & 0x00FF00 >> 8;
                int colorB = _color & 0x0000FF;
                if (_color == 0xFF)
                {
                    newPx[x * h + y] = Color.rgb(colorR, colorG, colorB);
                    continue ;
                }
                if (a == 0)
                    continue ;

                int t = 0xFF - a ;
                newPx[x * h + y] = Color.rgb((colorR * a + r * t) / 0xFF,
                        (colorG * a + g * t) / 0xFF, (colorB * a + b * t) / 0xFF);
            }
        }
        result.setPixels(newPx, 0, w, 0, 0, w, h);
        return result;
    }

    /**
     * 素描效果
     * @param bmp
     * @return
     */
    public static Bitmap convertToSketch(Bitmap bmp) {
        int pos, row, col, clr;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixSrc = new int[width * height];
        int[] pixNvt = new int[width * height];
        // 先对图象的像素处理成灰度颜色后再取反
        bmp.getPixels(pixSrc, 0, width, 0, 0, width, height);
        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                pos = row * width + col;
                pixSrc[pos] = (Color.red(pixSrc[pos])
                        + Color.green(pixSrc[pos]) + Color.blue(pixSrc[pos])) / 3;
                pixNvt[pos] = 255 - pixSrc[pos];
            }
        }
        // 对取反的像素进行高斯模糊, 强度可以设置，暂定为5.0
        gaussGray(pixNvt, 5.0, 5.0, width, height);
        // 灰度颜色和模糊后像素进行差值运算
        for (row = 0; row < height; row++) {
            for (col = 0; col < width; col++) {
                pos = row * width + col;
                clr = pixSrc[pos] << 8;
                clr /= 256 - pixNvt[pos];
                clr = Math.min(clr, 255);
                pixSrc[pos] = Color.rgb(clr, clr, clr);
            }
        }
        bmp.setPixels(pixSrc, 0, width, 0, 0, width, height);
        return bmp;
    }
    private static int gaussGray(int[] psrc, double horz, double vert,
                                 int width, int height) {
        int[] dst, src;
        double[] n_p, n_m, d_p, d_m, bd_p, bd_m;
        double[] val_p, val_m;
        int i, j, t, k, row, col, terms;
        int[] initial_p, initial_m;
        double std_dev;
        int row_stride = width;
        int max_len = Math.max(width, height);
        int sp_p_idx, sp_m_idx, vp_idx, vm_idx;
        val_p = new double[max_len];
        val_m = new double[max_len];
        n_p = new double[5];
        n_m = new double[5];
        d_p = new double[5];
        d_m = new double[5];
        bd_p = new double[5];
        bd_m = new double[5];
        src = new int[max_len];
        dst = new int[max_len];
        initial_p = new int[4];
        initial_m = new int[4];
        // 垂直方向
        if (vert > 0.0) {
            vert = Math.abs(vert) + 1.0;
            std_dev = Math.sqrt(-(vert * vert) / (2 * Math.log(1.0 / 255.0)));
            // 初试化常量
            findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);
            for (col = 0; col < width; col++) {
                for (k = 0; k < max_len; k++) {
                    val_m[k] = val_p[k] = 0;
                }
                for (t = 0; t < height; t++) {
                    src[t] = psrc[t * row_stride + col];
                }
                sp_p_idx = 0;
                sp_m_idx = height - 1;
                vp_idx = 0;
                vm_idx = height - 1;
                initial_p[0] = src[0];
                initial_m[0] = src[height - 1];
                for (row = 0; row < height; row++) {
                    terms = (row < 4) ? row : 4;
                    for (i = 0; i <= terms; i++) {
                        val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
                                * val_p[vp_idx - i];
                        val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
                                * val_m[vm_idx + i];
                    }
                    for (j = i; j <= 4; j++) {
                        val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
                        val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
                    }
                    sp_p_idx++;
                    sp_m_idx--;
                    vp_idx++;
                    vm_idx--;
                }
                transferGaussPixels(val_p, val_m, dst, 1, height);
                for (t = 0; t < height; t++) {
                    psrc[t * row_stride + col] = dst[t];
                }
            }
        }
        // 水平方向
        if (horz > 0.0) {
            horz = Math.abs(horz) + 1.0;
            if (horz != vert) {
                std_dev = Math.sqrt(-(horz * horz)
                        / (2 * Math.log(1.0 / 255.0)));
                // 初试化常量
                findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);
            }
            for (row = 0; row < height; row++) {
                for (k = 0; k < max_len; k++) {
                    val_m[k] = val_p[k] = 0;
                }
                for (t = 0; t < width; t++) {
                    src[t] = psrc[row * row_stride + t];
                }
                sp_p_idx = 0;
                sp_m_idx = width - 1;
                vp_idx = 0;
                vm_idx = width - 1;
                initial_p[0] = src[0];
                initial_m[0] = src[width - 1];
                for (col = 0; col < width; col++) {
                    terms = (col < 4) ? col : 4;
                    for (i = 0; i <= terms; i++) {
                        val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
                                * val_p[vp_idx - i];
                        val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
                                * val_m[vm_idx + i];
                    }
                    for (j = i; j <= 4; j++) {
                        val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
                        val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
                    }
                    sp_p_idx++;
                    sp_m_idx--;
                    vp_idx++;
                    vm_idx--;
                }
                transferGaussPixels(val_p, val_m, dst, 1, width);
                for (t = 0; t < width; t++) {
                    psrc[row * row_stride + t] = dst[t];
                }
            }
        }
        return 0;
    }
    private static void transferGaussPixels(double[] src1, double[] src2,
                                            int[] dest, int bytes, int width) {
        int i, j, k, b;
        int bend = bytes * width;
        double sum;
        i = j = k = 0;
        for (b = 0; b < bend; b++) {
            sum = src1[i++] + src2[j++];
            if (sum > 255)
                sum = 255;
            else if (sum < 0)
                sum = 0;
            dest[k++] = (int) sum;
        }
    }
    private static void findConstants(double[] n_p, double[] n_m, double[] d_p,
                                      double[] d_m, double[] bd_p, double[] bd_m, double std_dev) {
        double div = Math.sqrt(2 * 3.141593) * std_dev;
        double x0 = -1.783 / std_dev;
        double x1 = -1.723 / std_dev;
        double x2 = 0.6318 / std_dev;
        double x3 = 1.997 / std_dev;
        double x4 = 1.6803 / div;
        double x5 = 3.735 / div;
        double x6 = -0.6803 / div;
        double x7 = -0.2598 / div;
        int i;
        n_p[0] = x4 + x6;
        n_p[1] = (Math.exp(x1)
                * (x7 * Math.sin(x3) - (x6 + 2 * x4) * Math.cos(x3)) + Math
                .exp(x0) * (x5 * Math.sin(x2) - (2 * x6 + x4) * Math.cos(x2)));
        n_p[2] = (2
                * Math.exp(x0 + x1)
                * ((x4 + x6) * Math.cos(x3) * Math.cos(x2) - x5 * Math.cos(x3)
                * Math.sin(x2) - x7 * Math.cos(x2) * Math.sin(x3)) + x6
                * Math.exp(2 * x0) + x4 * Math.exp(2 * x1));
        n_p[3] = (Math.exp(x1 + 2 * x0)
                * (x7 * Math.sin(x3) - x6 * Math.cos(x3)) + Math.exp(x0 + 2
                * x1)
                * (x5 * Math.sin(x2) - x4 * Math.cos(x2)));
        n_p[4] = 0.0;
        d_p[0] = 0.0;
        d_p[1] = -2 * Math.exp(x1) * Math.cos(x3) - 2 * Math.exp(x0)
                * Math.cos(x2);
        d_p[2] = 4 * Math.cos(x3) * Math.cos(x2) * Math.exp(x0 + x1)
                + Math.exp(2 * x1) + Math.exp(2 * x0);
        d_p[3] = -2 * Math.cos(x2) * Math.exp(x0 + 2 * x1) - 2 * Math.cos(x3)
                * Math.exp(x1 + 2 * x0);
        d_p[4] = Math.exp(2 * x0 + 2 * x1);
        for (i = 0; i <= 4; i++) {
            d_m[i] = d_p[i];
        }
        n_m[0] = 0.0;
        for (i = 1; i <= 4; i++) {
            n_m[i] = n_p[i] - d_p[i] * n_p[0];
        }
        double sum_n_p, sum_n_m, sum_d;
        double a, b;
        sum_n_p = 0.0;
        sum_n_m = 0.0;
        sum_d = 0.0;
        for (i = 0; i <= 4; i++) {
            sum_n_p += n_p[i];
            sum_n_m += n_m[i];
            sum_d += d_p[i];
        }
        a = sum_n_p / (1.0 + sum_d);
        b = sum_n_m / (1.0 + sum_d);
        for (i = 0; i <= 4; i++) {
            bd_p[i] = d_p[i] * a;
            bd_m[i] = d_m[i] * b;
        }
    }
}

/*
 * Copyright (c) 2002-2010 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author suominka
 */
public class TextureLoader {
    private HashMap<String, Texture> textures;
    private IntBuffer textureIDBuffer = BufferUtils.createIntBuffer(1);
    private ColorModel glAlphaColorModel;
    private ColorModel glColorModel;
    
    public TextureLoader(){
        textures = new HashMap<String, Texture>();
        glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8,8,8,8}, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {8,8,8,0}, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
    }
    
    public Texture loadTexture(String path){
        Texture texture = textures.get(path);
        if( texture != null ){
            return texture;
        }
        
        texture = _loadTexture(path, GL_TEXTURE_2D, GL_RGBA, GL_NEAREST, GL_NEAREST);
        textures.put(path, texture);
        return texture;
    }
    
    private int createTextureID(){
        glGenTextures(textureIDBuffer);
        return textureIDBuffer.get(0);
    }
    
    private int toNearestPowerOf2(int a){
        int b = 2;
        while(b < a){
            b*=2;
        }
        return b;
    }
    
    private ByteBuffer convertImage(BufferedImage img, Texture tex){
        ByteBuffer imgBuffer;
        WritableRaster raster;
        BufferedImage texImage;
        
        int texW = toNearestPowerOf2(img.getWidth());
        int texH = toNearestPowerOf2(img.getHeight());
        tex.setTextureHeight(texH);
        tex.setTextureWidth(texW);
        
        int channels = 3;
        ColorModel colormodel;
        if( img.getColorModel().hasAlpha() ){
            channels = 4;
            colormodel = glAlphaColorModel;
        }else{
            colormodel = glColorModel;
        }

        raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texW, texH, channels, null);
        texImage = new BufferedImage(colormodel, raster, false, new Hashtable());
        
        Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f,0f,0f,0f));
        g.fillRect(0, 0, texW, texH);
        g.drawImage(img, 0, 0, null);
        
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
        
        imgBuffer = ByteBuffer.allocateDirect(data.length);
        imgBuffer.order(ByteOrder.nativeOrder());
        imgBuffer.put(data, 0, data.length);
        imgBuffer.flip();
        return imgBuffer;
    }
    
    private Texture _loadTexture(String path, int target, int pixelFormat, int minFilter, int magFilter){
        int srcPixelFormat = pixelFormat;
        int id = createTextureID();
        Texture texture = new Texture(target, id);
        glBindTexture(target,id);
        BufferedImage bufferedImg = loadImage(path);
        
        texture.setWidth(bufferedImg.getWidth());
        texture.setHeight(bufferedImg.getHeight());
        if( !bufferedImg.getColorModel().hasAlpha() ){
            srcPixelFormat = GL_RGB;
        }
        ByteBuffer textureBuffer = convertImage(bufferedImg,texture);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
        glTexImage2D(target, 0, pixelFormat, toNearestPowerOf2(bufferedImg.getWidth()), toNearestPowerOf2(bufferedImg.getHeight()), 0, srcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer);
        return texture;
    }
    
    private BufferedImage loadImage(String path){ //toimii
        try{
            File f = new File(path);
            BufferedImage source = ImageIO.read(f);
            int width = source.getWidth();
            int height = source.getHeight();
            BufferedImage bufferedImg = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedImg.getGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();
            return bufferedImg;
        }catch(Exception e){
        }
        return null;
    }
}

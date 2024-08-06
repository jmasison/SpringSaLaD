/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.springsalad.viewer;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
//import org.monte.media.av.Buffer;
//import org.monte.media.av.Format;
//import org.monte.media.av.MovieWriter;
//import org.monte.media.av.Registry;
//import org.monte.media.av.codec.video.VideoFormatKeys;
//import org.monte.media.avi.AVIWriter;
//import org.monte.media.math.Rational;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//import org.jcodec.api.awt.SequenceEncoder;
import javax.imageio.ImageIO;

public class MovieMaker {

    public static void makeMP4(File file, BufferedImage [] frames, int fps)
        throws IOException {
        SeekableByteChannel out = null;
        try {
            String outputVideoPath = file.getPath();
            out = NIOUtils.writableFileChannel(outputVideoPath);

            int width = frames[0].getWidth();
            int height = frames[0].getHeight();
            AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(fps, 1));
            for (BufferedImage frame : frames) {
                encoder.encodeImage(frame);
            }
            encoder.finish();

        } finally {
            NIOUtils.closeQuietly(out);
        }
    }
    
    public static void makeAnimagedGIF(File file, BufferedImage [] frames, int fps)
        throws IOException {
        
        try (ImageOutputStream output = new FileImageOutputStream(file)) {
            GifSequenceWriter gsw = new GifSequenceWriter(output,frames[0].getType(),fps*1000,false);

            for (BufferedImage bi1 : frames) {
                gsw.writeToSequence(bi1);
            }

            gsw.close();
        } catch(FileNotFoundException fne){
            fne.printStackTrace(System.out);
        } catch(IOException ioe){
            ioe.printStackTrace(System.out);
        }
    }
    
}

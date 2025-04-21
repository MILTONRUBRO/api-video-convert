package br.com.api.videoconvert.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

public class Test {

    public static void main(String[] args) {
        try {
            System.out.println("Processo iniciado:");

            String videoPath = "src/main/resources/video/Marvel_DOTNET_CSHARP.mp4";
            String outputFolder = "src/main/resources/images/";

            Files.createDirectories(Paths.get(outputFolder));

            FFprobe ffprobe = new FFprobe("src/main/resources/ffmfiles/ffprobe.exe");
            FFmpegProbeResult probeResult = ffprobe.probe(videoPath);
            FFmpegStream stream = probeResult.getStreams().get(0);
            double duration = stream.duration;

            double interval = 20.0;

            FFmpeg ffmpeg = new FFmpeg("src/main/resources/ffmfiles/ffmpeg.exe");

            for (double currentTime = 0; currentTime < duration; currentTime += interval) {
                System.out.println("Processando frame: " + currentTime);

                String outputPath = outputFolder + "frame_at_" + currentTime + ".jpg";
                FFmpegBuilder builder = new FFmpegBuilder()
                        .setInput(videoPath)
                        .addOutput(outputPath)
                        .setFrames(1)
                        .setVideoFilter("select='gte(t\\," + currentTime + ")'")
                        .done();

                ffmpeg.run(builder);
            }

            String destinationZipFilePath = "src/main/resources/images/images.zip";
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(destinationZipFilePath)))) {
                Files.walk(Paths.get(outputFolder)).filter(Files::isRegularFile).forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(Paths.get(outputFolder).relativize(path).toString());
                    try {
                        zos.putNextEntry(zipEntry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                });
            }

            System.out.println("Processo finalizado.");
        } catch (IOException e) {
            System.err.println("Erro durante o processo: " + e.getMessage());
        }
    }
}
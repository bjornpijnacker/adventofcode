package com.bjornp.aoc.util.viz;

import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/// This whole class is @SneakyThrows because this will never be production code. Failure is always final.
@Slf4j
public class Animation extends JFrame implements AutoCloseable {
    @Getter private final int width;

    @Getter private final int height;

    private final Process ffmpeg;

    private final OutputStream ffmpegIn;

    @SneakyThrows
    public Animation(AdventOfCodeSolution solution, String title, int width, int height, int fps) {
        var dir = Path.of("animations", String.valueOf(solution.getYear()), String.valueOf(solution.getDay()));

        var file = new File(dir.toFile(), "%s.mp4".formatted(title));
        if (!dir.toFile().isDirectory() && !dir.toFile().mkdirs()) {
            throw new RuntimeException("Could not create directory for animation frames: %s".formatted(dir));
        }

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-f",
                "image2pipe",
                "-vcodec",
                "png",
                "-r",
                String.valueOf(fps),
                "-i",
                "-",
                "-c:v",
                "libx265",
                "-pix_fmt",
                "yuv420p",
                "-b:v",
                "1M",
                file.getAbsolutePath()
        );
        ffmpeg = pb.start();
        ffmpegIn = ffmpeg.getOutputStream();

        this.width = width;
        this.height = height;

        // also set up JFrame for interactive viewing
        this.setSize(width, height);
        this.setPreferredSize(new Dimension(width, height));
        this.setTitle("%d/%d - %s".formatted(solution.getYear(), solution.getDay(), title));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public BufferedImage image() {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @SneakyThrows
    public void registerFrame(BufferedImage bi) {
        ImageIO.write(bi, "png", ffmpegIn);

        var image = bi.getScaledInstance(width, height, Image.SCALE_FAST);
        this.getGraphics().drawImage(image, 0, 0, null);
    }

    @SneakyThrows
    @Override
    public void close() throws IOException {
        log.info("Closing video encoder!");
        ffmpegIn.close();
        ffmpeg.waitFor();
        this.dispose();
    }
}

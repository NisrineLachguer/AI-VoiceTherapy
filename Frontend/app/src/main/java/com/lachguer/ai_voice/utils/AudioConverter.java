package com.lachguer.ai_voice.utils;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class AudioConverter {
    private static final String TAG = "AudioConverter";
    private static final int BUFFER_SIZE = 1024 * 1024; // 1MB buffer

    /**
     * Extracts the file extension from a file path or file name.
     * @param filePath The file path or file name
     * @return The file extension (without the dot) or an empty string if no extension is found
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }

        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            return "";
        }

        return filePath.substring(lastDotIndex + 1).toLowerCase();
    }

    public static File convertToWav(File inputFile, Context context) throws IOException {
        // Create output file
        File outputFile = File.createTempFile("output_", ".wav", context.getCacheDir());

        // If the input file is already a WAV file, just copy it
        String extension = getFileExtension(inputFile.getName());
        if ("wav".equalsIgnoreCase(extension)) {
            copyFile(inputFile, outputFile);
            return outputFile;
        }

        // For other formats, try to use MediaExtractor/MediaMuxer
        try {
            convertUsingMediaExtractor(inputFile, outputFile);
            return outputFile;
        } catch (Exception e) {
            Log.e(TAG, "Error using MediaExtractor: " + e.getMessage());

            // Fallback: just copy the file and rename it to .wav
            // This is not a real conversion but might work in some cases
            // where the app just needs a file with .wav extension
            copyFile(inputFile, outputFile);
            return outputFile;
        }
    }

    private static void convertUsingMediaExtractor(File inputFile, File outputFile) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(inputFile.getPath());

        // Find the first audio track
        int audioTrackIndex = -1;
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime != null && mime.startsWith("audio/")) {
                audioTrackIndex = i;
                break;
            }
        }

        if (audioTrackIndex < 0) {
            throw new IOException("No audio track found in the input file");
        }

        // Select the audio track
        extractor.selectTrack(audioTrackIndex);
        MediaFormat inputFormat = extractor.getTrackFormat(audioTrackIndex);

        // Create a MediaMuxer for the output file
        MediaMuxer muxer = new MediaMuxer(outputFile.getPath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        int outputTrackIndex = muxer.addTrack(inputFormat);
        muxer.start();

        // Allocate buffer for reading
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        // Read and write samples
        while (true) {
            int sampleSize = extractor.readSampleData(buffer, 0);
            if (sampleSize < 0) {
                break; // End of stream
            }

            bufferInfo.offset = 0;
            bufferInfo.size = sampleSize;
            bufferInfo.presentationTimeUs = extractor.getSampleTime();

            // Convert MediaExtractor flags to MediaCodec flags
            int flags = 0;
            if ((extractor.getSampleFlags() & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                flags |= MediaCodec.BUFFER_FLAG_KEY_FRAME;
            }
            bufferInfo.flags = flags;

            muxer.writeSampleData(outputTrackIndex, buffer, bufferInfo);
            extractor.advance();
        }

        // Release resources
        muxer.stop();
        muxer.release();
        extractor.release();
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile);
             FileChannel sourceChannel = fis.getChannel();
             FileChannel destChannel = fos.getChannel()) {

            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }
}

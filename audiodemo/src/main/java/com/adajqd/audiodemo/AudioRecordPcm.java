/**
 * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
 */
private static final int SAMPLE_RATE_INHZ = 44100;

/**
 * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
 */
private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
/**
 * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
 */
private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
    CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

final byte data[] = new byte[minBufferSize];
final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
if (!file.mkdirs()) {
    Log.e(TAG, "Directory not created");
}
if (file.exists()) {
    file.delete();
}

audioRecord.startRecording();
isRecording = true;

new Thread(new Runnable() {
    @Override public void run() {

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (null != os) {
            while (isRecording) {
                int read = audioRecord.read(data, 0, minBufferSize);
                // 如果读取音频数据没有出现错误，就将数据写入到文件
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Log.i(TAG, "run: close file output stream !");
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}).start();

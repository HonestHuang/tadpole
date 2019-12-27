package studio.littlefrog.tadpole.recorder;

public class StringRecorder implements IRecorder {
    private StringBuffer stringBuffer;

    public StringRecorder(StringBuffer stringBuffer) {
        this.stringBuffer = stringBuffer;
    }

    @Override
    public IRecorder append(String message) {
        stringBuffer.append(message);
        return this;
    }

    @Override
    public IRecorder br(Integer num) {
        return this;
    }

    @Override
    public void close() throws Exception {
    }
}

package studio.littlefrog.tadpole.recorder;

public class JdkRecorder implements IRecorder {
    @Override
    public IRecorder append(String message) {
        System.out.print(message);
        return this;
    }

    @Override
    public IRecorder br(Integer num) {
        System.out.println("");
        return this;
    }

    @Override
    public void close() throws Exception {

    }
}

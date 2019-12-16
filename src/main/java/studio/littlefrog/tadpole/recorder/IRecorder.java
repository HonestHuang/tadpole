package studio.littlefrog.tadpole.recorder;

public interface IRecorder extends AutoCloseable {
    IRecorder append(String message);

    IRecorder br();

    IRecorder br(Integer num);
}

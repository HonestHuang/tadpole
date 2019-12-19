package studio.littlefrog.tadpole.recorder;

public interface IRecorder extends AutoCloseable {
    String lineSeparator = System.getProperty("line.separator");

    IRecorder append(String message);

    default IRecorder br(){
        return br(1);
    }

    IRecorder br(Integer num);
}

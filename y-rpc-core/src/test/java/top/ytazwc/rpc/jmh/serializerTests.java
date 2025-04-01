package top.ytazwc.rpc.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import top.ytazwc.rpc.entity.RpcRequest;
import top.ytazwc.rpc.serializer.kryo.KryoSerializer;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
public class serializerTests {

    private RpcRequest testObject;
    KryoSerializer kryoSerializer;

    @Setup(Level.Trial)
    public void setup() {
        testObject = RpcRequest.builder().methodName("hello")
                .parameters(new Object[]{"sayheloossssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssloo", "sayhelooloosayhelooloo"})
                .interfaceName("top.ytazwc.rpc")
                .paramTypes(new Class<?>[]{String.class, String.class})
                .requestId(UUID.randomUUID().toString())
                .group("group1")
                .version("version1")
                .build();
        kryoSerializer = new KryoSerializer();
    }

    @Benchmark()
    public void testKryoSerialization() {
        kryoSerializer.serialize(testObject); // 使用KryoSerializer.serialize方法
    }

    @Benchmark
    public void testKryoDeserialization() {
        byte[] serializedBytes = kryoSerializer.serialize(testObject); // 序列化对象以获取字节数组
        kryoSerializer.deserialize(serializedBytes, RpcRequest.class); // 使用KryoSerializer.deserialize方法
    }

    @Benchmark
    public void testJavaSerialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(testObject);
        objectOutputStream.close();
        byteArrayOutputStream.close();
    }

    @Benchmark
    public void testJavaDeserialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(testObject);
        objectOutputStream.close();
        byteArrayOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(serializerTests.class.getSimpleName())
                .forks(10)
                .resultFormat(ResultFormatType.JSON)
                .result("/D:/data/result.json")
                .build();

        new Runner(opt).run();
    }

}

package ku.cs.services;

import java.io.IOException;

public interface Datasource<T> {
    T readData();
    void writeData(T data);
}
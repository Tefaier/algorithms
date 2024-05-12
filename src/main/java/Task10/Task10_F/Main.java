package Task10.Task10_F;

import java.io.DataInputStream;
import java.io.InputStream;

public class Main {
  private static Parser in = new Parser(System.in);

  public static void main(String[] args) {
    int sizeA = in.nextInt();
    int[] polA = new int[sizeA + 1];
    for (int i = 0; i <= sizeA; i++) {
      polA[i] = in.nextInt();
    }
    int sizeB = in.nextInt();
    int[] polB = new int[sizeB + 1];
    for (int i = 0; i <= sizeB; i++) {
      polB[i] = in.nextInt();
    }
    var result = FFT.multiplyPolinoms(polA, polB);
    StringBuilder output = new StringBuilder();
    output.append(sizeA + sizeB).append(" ");
    for (int i = 0; i <= sizeA + sizeB; ++i) {
      output.append(result[i]).append(" ");
    }
    System.out.println(output);
  }
}

// source is https://habr.com/ru/articles/91283/
class Parser {
  private final int BUFFER_SIZE = 1 << 16;
  private DataInputStream din;
  private byte[] buffer;
  private int bufferPointer, bytesRead;

  public Parser(InputStream in) {
    din = new DataInputStream(in);
    buffer = new byte[BUFFER_SIZE];
    bufferPointer = bytesRead = 0;
  }

  public String nextString(int maxSize) {
    byte[] ch = new byte[maxSize];
    int point = 0;
    try {
      byte c = read();
      while (c == ' ' || c == '\n' || c == '\r') {
        c = read();
      }
      while (c != ' ' && c != '\n' && c != '\r') {
        ch[point++] = c;
        c = read();
      }
    } catch (Exception e) {
    }
    return new String(ch, 0, point);
  }

  public int nextInt() {
    int ret = 0;
    boolean neg;
    try {
      byte c = read();
      while (c <= ' ') {
        c = read();
      }
      neg = c == '-';
      if (neg) {
        c = read();
      }
      do {
        ret = ret * 10 + c - '0';
        c = read();
      } while (c > ' ');

      if (neg) {
        return -ret;
      }
    } catch (Exception e) {
    }
    return ret;
  }

  public long nextLong() {
    long ret = 0;
    boolean neg;
    try {
      byte c = read();
      while (c <= ' ') {
        c = read();
      }
      neg = c == '-';
      if (neg) {
        c = read();
      }
      do {
        ret = ret * 10 + c - '0';
        c = read();
      } while (c > ' ');

      if (neg) {
        return -ret;
      }
    } catch (Exception e) {
    }
    return ret;
  }

  private void fillBuffer() {
    try {
      bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
    } catch (Exception e) {
    }
    if (bytesRead == -1) buffer[0] = -1;
  }

  private byte read() {
    if (bufferPointer == bytesRead) {
      fillBuffer();
    }
    return buffer[bufferPointer++];
  }
}

class FFT {
  public static long[] multiplyPolinoms(int[] pol1, int[] pol2) {
    int maxInputSize = Math.max(pol1.length, pol2.length);
    int size = 1;
    while (size < maxInputSize) size <<= 1;
    size <<= 1;

    Complex[] pol1Complex = new Complex[size];
    Complex[] pol2Complex = new Complex[size];
    for (int i = 0; i < size; i++) {
      pol1Complex[i] = new Complex(i < pol1.length ? pol1[i] : 0, 0);
      pol2Complex[i] = new Complex(i < pol2.length ? pol2[i] : 0, 0);
    }

    fft(pol1Complex, false);
    fft(pol2Complex, false);

    // combine values
    for (int i = 0; i < size; ++i) {
      pol1Complex[i].multiply(pol2Complex[i]);
    }
    fft(pol1Complex, true);

    // reverse parsing from complex numbers
    long[] result = new long[size];
    for (int i = 0; i < size; i++) {
      result[i] = Math.round(pol1Complex[i].real);
    }

    return result;
  }

  public static void fft(Complex[] values, boolean invert) {
    int size = values.length;
    int sizeHalved = size / 2;
    if (size == 1) return;

    Complex[] even = new Complex[sizeHalved];
    for (int k = 0; k < sizeHalved; k++) {
      even[k] = values[2 * k];
    }
    Complex[] odd = new Complex[sizeHalved];
    for (int k = 0; k < sizeHalved; k++) {
      odd[k] = values[2 * k + 1];
    }

    fft(even, invert);
    fft(odd, invert);

    Complex w = new Complex(1, 0);
    double angle = 2 * Math.PI / size * (invert ? -1 : 1);
    Complex wAdd = new Complex(Math.cos(angle), Math.sin(angle));
    for (int k = 0; k < sizeHalved; k++) {
      Complex secondPart = Complex.multiply(w, odd[k]);
      values[k] = Complex.add(even[k], secondPart);
      values[k + sizeHalved] = Complex.subtract(even[k], secondPart);
      if (invert) {
        values[k].simpleDivide(2);
        values[k + sizeHalved].simpleDivide(2);
      }
      w.multiply(wAdd);
    }
  }
}

// source is https://stackoverflow.com/questions/2997053/does-java-have-a-class-for-complex-numbers - was simplified
class Complex {
  public double real;
  public double imaginary;

  public Complex(double real, double imaginary) {
    this.real = real;
    this.imaginary = imaginary;
  }

  public void multiply(Complex z) {
    set(multiply(this, z));
  }

  public void simpleDivide(double x) {
    real /= x;
    imaginary /= x;
  }

  public void set(Complex z) {
    this.real = z.real;
    this.imaginary = z.imaginary;
  }

  public static Complex add(Complex z1, Complex z2) {
    return new Complex(z1.real + z2.real, z1.imaginary + z2.imaginary);
  }

  public static Complex subtract(Complex z1, Complex z2) {
    return new Complex(z1.real - z2.real, z1.imaginary - z2.imaginary);
  }

  public static Complex multiply(Complex z1, Complex z2) {
    return new Complex(z1.real * z2.real - z1.imaginary * z2.imaginary, z1.real * z2.imaginary + z1.imaginary * z2.real);
  }
}

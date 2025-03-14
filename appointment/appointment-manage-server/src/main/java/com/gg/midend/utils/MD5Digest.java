package com.gg.midend.utils;

/**
 * 描述：源启MD5加密类
 */
public class MD5Digest {
  
  private final byte[] xBuf = new byte[4];
  
  private int xBufOff;
  
  private long byteCount;
  
  private int H1;
  
  private int H2;
  
  private int H3;
  
  private int H4;
  
  private int[] X = new int[16];
  
  private int xOff;
  
  public MD5Digest() {
    this.xBufOff = 0;
    reset();
  }
  
  private MD5Digest(MD5Digest t) {
    copyIn(t);
  }
  
  private void copyIn(MD5Digest t) {
    System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);
    this.xBufOff = t.xBufOff;
    this.byteCount = t.byteCount;
    this.H1 = t.H1;
    this.H2 = t.H2;
    this.H3 = t.H3;
    this.H4 = t.H4;
    System.arraycopy(t.X, 0, this.X, 0, t.X.length);
    this.xOff = t.xOff;
  }
  
  public MD5Digest copy() {
    return new MD5Digest(this);
  }
  
  protected void processWord(byte[] in, int inOff) {
    this.X[this.xOff++] = in[inOff] & 0xFF | (in[inOff + 1] & 0xFF) << 8 | (in[inOff + 2] & 0xFF) << 16 | (in[inOff + 3] & 0xFF) << 24;
    if (this.xOff == 16)
      processBlock(); 
  }
  
  protected void processLength(long bitLength) {
    if (this.xOff > 14)
      processBlock(); 
    this.X[14] = (int)(bitLength & 0xFFFFFFFFFFFFFFFFL);
    this.X[15] = (int)(bitLength >>> 32L);
  }
  
  private void unpackWord(int word, byte[] out, int outOff) {
    out[outOff] = (byte)word;
    out[outOff + 1] = (byte)(word >>> 8);
    out[outOff + 2] = (byte)(word >>> 16);
    out[outOff + 3] = (byte)(word >>> 24);
  }
  
  public int doFinal(byte[] out, int outOff) {
    finish();
    unpackWord(this.H1, out, outOff);
    unpackWord(this.H2, out, outOff + 4);
    unpackWord(this.H3, out, outOff + 8);
    unpackWord(this.H4, out, outOff + 12);
    reset();
    return 16;
  }
  
  public void finish() {
    long bitLength = this.byteCount << 3L;
    update((byte) -128);
    while (this.xBufOff != 0)
      update((byte)0); 
    processLength(bitLength);
    processBlock();
  }
  
  public void reset() {
    this.byteCount = 0L;
    this.xBufOff = 0;
    int i;
    for (i = 0; i < this.xBuf.length; i++)
      this.xBuf[i] = 0; 
    this.H1 = 1732584193;
    this.H2 = -271733879;
    this.H3 = -1732584194;
    this.H4 = 271733878;
    this.xOff = 0;
    for (i = 0; i != this.X.length; i++)
      this.X[i] = 0; 
  }
  
  public void update(byte in) {
    this.xBuf[this.xBufOff++] = in;
    if (this.xBufOff == this.xBuf.length) {
      processWord(this.xBuf, 0);
      this.xBufOff = 0;
    } 
    this.byteCount++;
  }
  
  public void update(byte[] in, int inOff, int len) {
    len = Math.max(0, len);
    int i = 0;
    if (this.xBufOff != 0)
      while (i < len) {
        this.xBuf[this.xBufOff++] = in[inOff + i++];
        if (this.xBufOff == 4) {
          processWord(this.xBuf, 0);
          this.xBufOff = 0;
          break;
        } 
      }  
    int limit = (len - i & 0xFFFFFFFC) + i;
    for (; i < limit; i += 4)
      processWord(in, inOff + i); 
    while (i < len)
      this.xBuf[this.xBufOff++] = in[inOff + i++]; 
    this.byteCount += len;
  }
  
  private int rotateLeft(int x, int n) {
    return x << n | x >>> 32 - n;
  }
  
  private int F(int u, int v, int w) {
    return u & v | (u ^ 0xFFFFFFFF) & w;
  }
  
  private int G(int u, int v, int w) {
    return u & w | v & (w ^ 0xFFFFFFFF);
  }
  
  private int H(int u, int v, int w) {
    return u ^ v ^ w;
  }
  
  private int K(int u, int v, int w) {
    return v ^ (u | w ^ 0xFFFFFFFF);
  }
  
  protected void processBlock() {
    int a = this.H1;
    int b = this.H2;
    int c = this.H3;
    int d = this.H4;
    a = rotateLeft(a + F(b, c, d) + this.X[0] + -680876936, 7) + b;
    d = rotateLeft(d + F(a, b, c) + this.X[1] + -389564586, 12) + a;
    c = rotateLeft(c + F(d, a, b) + this.X[2] + 606105819, 17) + d;
    b = rotateLeft(b + F(c, d, a) + this.X[3] + -1044525330, 22) + c;
    a = rotateLeft(a + F(b, c, d) + this.X[4] + -176418897, 7) + b;
    d = rotateLeft(d + F(a, b, c) + this.X[5] + 1200080426, 12) + a;
    c = rotateLeft(c + F(d, a, b) + this.X[6] + -1473231341, 17) + d;
    b = rotateLeft(b + F(c, d, a) + this.X[7] + -45705983, 22) + c;
    a = rotateLeft(a + F(b, c, d) + this.X[8] + 1770035416, 7) + b;
    d = rotateLeft(d + F(a, b, c) + this.X[9] + -1958414417, 12) + a;
    c = rotateLeft(c + F(d, a, b) + this.X[10] + -42063, 17) + d;
    b = rotateLeft(b + F(c, d, a) + this.X[11] + -1990404162, 22) + c;
    a = rotateLeft(a + F(b, c, d) + this.X[12] + 1804603682, 7) + b;
    d = rotateLeft(d + F(a, b, c) + this.X[13] + -40341101, 12) + a;
    c = rotateLeft(c + F(d, a, b) + this.X[14] + -1502002290, 17) + d;
    b = rotateLeft(b + F(c, d, a) + this.X[15] + 1236535329, 22) + c;
    a = rotateLeft(a + G(b, c, d) + this.X[1] + -165796510, 5) + b;
    d = rotateLeft(d + G(a, b, c) + this.X[6] + -1069501632, 9) + a;
    c = rotateLeft(c + G(d, a, b) + this.X[11] + 643717713, 14) + d;
    b = rotateLeft(b + G(c, d, a) + this.X[0] + -373897302, 20) + c;
    a = rotateLeft(a + G(b, c, d) + this.X[5] + -701558691, 5) + b;
    d = rotateLeft(d + G(a, b, c) + this.X[10] + 38016083, 9) + a;
    c = rotateLeft(c + G(d, a, b) + this.X[15] + -660478335, 14) + d;
    b = rotateLeft(b + G(c, d, a) + this.X[4] + -405537848, 20) + c;
    a = rotateLeft(a + G(b, c, d) + this.X[9] + 568446438, 5) + b;
    d = rotateLeft(d + G(a, b, c) + this.X[14] + -1019803690, 9) + a;
    c = rotateLeft(c + G(d, a, b) + this.X[3] + -187363961, 14) + d;
    b = rotateLeft(b + G(c, d, a) + this.X[8] + 1163531501, 20) + c;
    a = rotateLeft(a + G(b, c, d) + this.X[13] + -1444681467, 5) + b;
    d = rotateLeft(d + G(a, b, c) + this.X[2] + -51403784, 9) + a;
    c = rotateLeft(c + G(d, a, b) + this.X[7] + 1735328473, 14) + d;
    b = rotateLeft(b + G(c, d, a) + this.X[12] + -1926607734, 20) + c;
    a = rotateLeft(a + H(b, c, d) + this.X[5] + -378558, 4) + b;
    d = rotateLeft(d + H(a, b, c) + this.X[8] + -2022574463, 11) + a;
    c = rotateLeft(c + H(d, a, b) + this.X[11] + 1839030562, 16) + d;
    b = rotateLeft(b + H(c, d, a) + this.X[14] + -35309556, 23) + c;
    a = rotateLeft(a + H(b, c, d) + this.X[1] + -1530992060, 4) + b;
    d = rotateLeft(d + H(a, b, c) + this.X[4] + 1272893353, 11) + a;
    c = rotateLeft(c + H(d, a, b) + this.X[7] + -155497632, 16) + d;
    b = rotateLeft(b + H(c, d, a) + this.X[10] + -1094730640, 23) + c;
    a = rotateLeft(a + H(b, c, d) + this.X[13] + 681279174, 4) + b;
    d = rotateLeft(d + H(a, b, c) + this.X[0] + -358537222, 11) + a;
    c = rotateLeft(c + H(d, a, b) + this.X[3] + -722521979, 16) + d;
    b = rotateLeft(b + H(c, d, a) + this.X[6] + 76029189, 23) + c;
    a = rotateLeft(a + H(b, c, d) + this.X[9] + -640364487, 4) + b;
    d = rotateLeft(d + H(a, b, c) + this.X[12] + -421815835, 11) + a;
    c = rotateLeft(c + H(d, a, b) + this.X[15] + 530742520, 16) + d;
    b = rotateLeft(b + H(c, d, a) + this.X[2] + -995338651, 23) + c;
    a = rotateLeft(a + K(b, c, d) + this.X[0] + -198630844, 6) + b;
    d = rotateLeft(d + K(a, b, c) + this.X[7] + 1126891415, 10) + a;
    c = rotateLeft(c + K(d, a, b) + this.X[14] + -1416354905, 15) + d;
    b = rotateLeft(b + K(c, d, a) + this.X[5] + -57434055, 21) + c;
    a = rotateLeft(a + K(b, c, d) + this.X[12] + 1700485571, 6) + b;
    d = rotateLeft(d + K(a, b, c) + this.X[3] + -1894986606, 10) + a;
    c = rotateLeft(c + K(d, a, b) + this.X[10] + -1051523, 15) + d;
    b = rotateLeft(b + K(c, d, a) + this.X[1] + -2054922799, 21) + c;
    a = rotateLeft(a + K(b, c, d) + this.X[8] + 1873313359, 6) + b;
    d = rotateLeft(d + K(a, b, c) + this.X[15] + -30611744, 10) + a;
    c = rotateLeft(c + K(d, a, b) + this.X[6] + -1560198380, 15) + d;
    b = rotateLeft(b + K(c, d, a) + this.X[13] + 1309151649, 21) + c;
    a = rotateLeft(a + K(b, c, d) + this.X[4] + -145523070, 6) + b;
    d = rotateLeft(d + K(a, b, c) + this.X[11] + -1120210379, 10) + a;
    c = rotateLeft(c + K(d, a, b) + this.X[2] + 718787259, 15) + d;
    b = rotateLeft(b + K(c, d, a) + this.X[9] + -343485551, 21) + c;
    this.H1 += a;
    this.H2 += b;
    this.H3 += c;
    this.H4 += d;
    this.xOff = 0;
    for (int i = 0; i != this.X.length; i++)
      this.X[i] = 0; 
  }
  
}
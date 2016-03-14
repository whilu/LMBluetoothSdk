/*
 * The MIT License (MIT)

 * Copyright (c) 2015 LinkMob.cc

 * Author: lujun

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.lujun.lmbluetoothsdk.base;

/**
 * Author: lujun(http://blog.lujun.co)
 * Date: 2016-1-19 12:12
 */
public class State {

    /** we're doing nothing*/
    public static final int STATE_NONE = 0;

    /** now listening for incoming connections*/
    public static final int STATE_LISTEN = 1;

    /** now initiating an outgoing connection*/
    public static final int STATE_CONNECTING = 2;

    /** now connected to a remote device*/
    public static final int STATE_CONNECTED = 3;

    /** lost the connection*/
    public static final int STATE_DISCONNECTED = 4;

    /** unknown state*/
    public static final int STATE_UNKNOWN = 5;

    /** got all characteristics*/
    public static final int STATE_GOT_CHARACTERISTICS = 6;
}

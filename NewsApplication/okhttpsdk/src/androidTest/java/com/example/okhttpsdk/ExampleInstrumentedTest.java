package com.example.okhttpsdk;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.CommonOkHttpClient;
import com.example.request.CommonRequest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.okhttpsdk", appContext.getPackageName());
    }

    //网络请求
    @Test
    public void commonOkHttpClientTest(){

//        //callback未封装
//        CommonOkHttpClient.sendRequest(CommonRequest.creatGetRequest("http://www.imook.com", null), new Callback() {
//            //请求错误
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//             //处理异常 数据解析 数据转发
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });
    }

}

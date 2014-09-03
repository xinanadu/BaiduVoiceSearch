package info.zhegui.baidu_voice_search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ActivityMain extends Activity implements Handler.Callback {


    private BaiduASRDigitalDialog mDialog = null;

    private DialogRecognitionListener mRecognitionListener;

    private int mCurrentTheme = Config.DIALOG_THEME;

    private EditText etKeyword;
    private ImageView ivDel;

    private Handler mHandler;

    private static final int WHAT_SHOW_SOFT_INPUT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(this);

        etKeyword = (EditText) findViewById(R.id.et_keyword);
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //log("beforeTextChanged(" + s + ")");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //log("onTextChanged(" + s + ")");
            }

            @Override
            public void afterTextChanged(Editable s) {
                //log("afterTextChanged(" + s.toString() + ")");
                if(s.toString().length()==0){
                    ivDel.setVisibility(View.GONE);
                }else{
                    ivDel.setVisibility(View.VISIBLE);
                }
            }
        });

        ivDel = (ImageView) findViewById(R.id.iv_del);
        ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etKeyword.setText(null);
            }
        });

        Button btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = etKeyword.getText().toString().trim();
                if (keyword.length() == 0) {
                    toast("Please input something...");
                } else {
                    try {
                        keyword = URLEncoder.encode(keyword, "utf-8");
                    } catch (UnsupportedEncodingException e) {

                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.baidu.com/s?wd=" + keyword));
                    ActivityMain.this.startActivity(intent);
                }
            }
        });

        ImageView ibtnMic = (ImageView) findViewById(R.id.ibtn_mike);
        ibtnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToVoice();
            }
        });

        mRecognitionListener = new DialogRecognitionListener() {

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                for (int i = 0; rs != null && i < rs.size(); i++) {
                    log("-->" + rs.get(i));
                }
                if (rs != null && rs.size() > 0) {
                    etKeyword.setText(rs.get(0));
                }

            }
        };

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("voice", false)) {
            listenToVoice();
        }

        mHandler.sendEmptyMessageDelayed(WHAT_SHOW_SOFT_INPUT, 500);
    }

    private void showInput() {
        log("showInput()");

        InputMethodManager inputManager = (InputMethodManager) etKeyword
                .getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);

        inputManager.showSoftInput(etKeyword, InputMethodManager.SHOW_FORCED);
    }

    private void listenToVoice() {
        log("listToVoice()");
        etKeyword.setText(null);
        if (mDialog == null || mCurrentTheme != Config.DIALOG_THEME) {
            mCurrentTheme = Config.DIALOG_THEME;
            if (mDialog != null) {
                mDialog.dismiss();
            }
            Bundle params = new Bundle();
            params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
            params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
            params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
            mDialog = new BaiduASRDigitalDialog(ActivityMain.this, params);
            mDialog.setDialogRecognitionListener(mRecognitionListener);
        }
        mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
        mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                Config.getCurrentLanguage());
        Log.e("DEBUG", "Config.PLAY_START_SOUND = " + Config.PLAY_START_SOUND);
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
        mDialog.show();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_SHOW_SOFT_INPUT:
                showInput();

                break;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        log("onNewIntent(" + intent + ")");

        if (intent != null && intent.getBooleanExtra("voice", false)) {
            listenToVoice();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void log(String text) {
        Log.i("ActivityMain", text);
    }
}

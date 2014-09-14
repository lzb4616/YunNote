package com.app.linyu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.app.linyu.R;

/**
 * com.app.linyu.activity
 * Created by zibin on 2014/4/9 0009.
 */
public class LoginActivity extends Activity{
    private static final String USERNAME = "linyu";
    private static final String USERPASSWORD = "linyu";
    private EditText user_name_et;
    private  EditText user_pw_et;
    private Button login_btn;
    private AlertDialog.Builder mDialogBuilder  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_name_et = (EditText)findViewById(R.id.user_name);
        user_pw_et = (EditText)findViewById(R.id.user_password);
        login_btn = (Button)findViewById(R.id.login_button);
        mDialogBuilder  = new AlertDialog.Builder(LoginActivity.this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user_name_et.getText().toString().equals(USERNAME)&&user_pw_et.getText().toString().equals(USERPASSWORD)){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if (user_name_et.getText().equals("")&&user_name_et.getText().equals(null)){

                    mDialogBuilder.setMessage("用户名不能为空");
                    mDialogBuilder.setNegativeButton("确定",null);
                    mDialogBuilder.show();
                }else if(!user_name_et.getText().toString().equals(USERNAME)){
                    mDialogBuilder.setMessage("用户名不对，请重新输入");
                    mDialogBuilder.setNegativeButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user_name_et.setText("");
                        }
                    });
                    mDialogBuilder.show();
                }else if (user_pw_et.getText().equals("")&&user_pw_et.getText().equals(null)){

                    mDialogBuilder.setMessage("密码不能为空");
                    mDialogBuilder.setNegativeButton("确定",null);
                    mDialogBuilder.show();
                }else if(!user_pw_et.getText().toString().equals(USERPASSWORD)){
                    mDialogBuilder.setMessage("密码不对，请重新输入");
                    mDialogBuilder.setNegativeButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            user_pw_et.setText("");
                        }
                    });
                    mDialogBuilder.show();
                }



            }
        });



    }
}

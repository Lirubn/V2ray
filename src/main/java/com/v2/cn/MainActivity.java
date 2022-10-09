package com.v2.cn;

import com.v2.cn.ZipUtils;
import com.v2.cn.ShellUtils;
import com.v2.cn.v2ray;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.Toast;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.orhanobut.dialogplus.*;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;
import com.github.ybq.android.spinkit.*;
import android.widget.ImageView;
//import com.sun.tools.javac.util.List;



public class MainActivity extends Activity {
	//开关文本控件
	private static TextView text1;
	private static TextView text2;
	//shell返回值
	public static ShellUtils.CommandResult Shell_ret = null;
	//内部存储目录
	public static String path = null;
	//开关加载控件
	private static SpinKitView progressBar;
	//开关图片
	private static ImageView imageView;
	//mian_context
	private static Context context_log = null;
	//预选代理
	public static String CurrentAgentInformation = null;
	//模块开启状态
	public static boolean ModuleStatus = false;
	//开关加载状态
	public static boolean SwitchLoadingState = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //沉浸导航栏
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
		path = getFilesDir() + "";
		ControlInitialization();
		context_log = this;
		new Thread(new Runnable() {//创建子线程
				@Override
				public void run() {
					SwitchLoadingState = true;
					v2ray.v2rayFileInit(MainActivity.this, path);
					updateUI(MainActivity.this);
				}
			}).start();//启动子线程
    }

	/*
	 线程回调
	 */
	public void updateUI(final Context context) { ((MainActivity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (v2ray.fileIsExists(path + "/CurrentAgentInformation.txt") && v2ray.fileIsExists(v2ray.readFile(path + "/CurrentAgentInformation.txt"))) {
						CurrentAgentInformation = v2ray.readFile(path + "/CurrentAgentInformation.txt");
					}
					GetStateAgent();
				}

			});
	}

	public static void GetStateAgent() {
		progressBar.setVisibility(View.GONE);
		imageView.setImageResource(R.drawable.ic_vpn_lock_black_48dp);
		imageView.setVisibility(View.VISIBLE);
		if (v2ray.StringJudgment(Shell_ret.successMsg, "是否正确") && v2ray.getFilesAllName(path + "/节点选择/").size() < 1) {
			//定义颜色
			int checkColor = context_log.getResources().getColor(R.color.text_but);
			imageView.setColorFilter(checkColor);
			text1.setText("未激活模块");
			text2.setText("代理文件不存在！");
		}
		if (v2ray.StringJudgment(Shell_ret.successMsg, "❌") || v2ray.getFilesAllName(path + "/节点选择/").size() > 1) {
			//定义颜色
			int checkColor = context_log.getResources().getColor(R.color.text_but);
			imageView.setColorFilter(checkColor);
			text1.setText("未激活模块");
			if (CurrentAgentInformation == null) {
			    text2.setText("混淆：" + v2ray.readFile(path + "/hosts.txt"));
			} else {
				text2.setText("混淆：" + v2ray.readFile(path + "/hosts.txt") + "\n代理：" + CurrentAgentInformation.split(".sh")[0].split("节点选择/")[1]);
			}
		}
		if (v2ray.StringJudgment(Shell_ret.successMsg, "联网失败") && v2ray.StringJudgment(Shell_ret.successMsg, "✔️")) {
			ModuleStatus = true;
			imageView.setImageResource(R.drawable.ic_check_black_48dp);
			//定义颜色
			int checkColor = context_log.getResources().getColor(R.color.colorv2rays);
			imageView.setColorFilter(checkColor);
			text1.setText("模块已激活");
			text2.setText("混淆：" + v2ray.readFile(path + "/hosts.txt") + "\n代理：" + CurrentAgentInformation.split(".sh")[0].split("节点选择/")[1]);
			AlertDialog dialog = new AlertDialog.Builder(context_log)
				.setTitle("警告!")
				.setMessage(Shell_ret.successMsg.split("DNS")[1].split("节点名")[0])
				.create();
			dialog.show();
		}
		if (!v2ray.StringJudgment(Shell_ret.successMsg, "联网失败") && v2ray.StringJudgment(Shell_ret.successMsg, "✔️")) {
			ModuleStatus = true;
			imageView.setImageResource(R.drawable.ic_done_all_black_48dp);
			//定义颜色
			int checkColor = context_log.getResources().getColor(R.color.colorMainv2ray);
			imageView.setColorFilter(checkColor);
			text1.setText("模块已激活");
			text2.setText("混淆：" + v2ray.readFile(path + "/hosts.txt") + "\n代理：" + CurrentAgentInformation.split(".sh")[0].split("节点选择/")[1]);
		}
		if (Shell_ret.errorMsg.length() > 3) {
			AlertDialog dialog = new AlertDialog.Builder(context_log)
				.setTitle("Error!")
				.setMessage(Shell_ret.errorMsg)
				.create();
			dialog.show();
		}
		SwitchLoadingState = false;
	}
	
	public static void  updateUI_shell(final Context context) { ((MainActivity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (v2ray.StringJudgment(Shell_ret.errorMsg + "", "100")) {
						progressBar.setVisibility(View.GONE);
						imageView.setImageResource(R.drawable.ic_cloud_done_black_48dp);
						imageView.setVisibility(View.VISIBLE);
						text1.setText("获取成功！");
						List<String> arr = v2ray.getFilesAllName(path + "/节点选择/");
						text2.setText("已获取" + arr.size() + "个节点");
						//ToastUtil.show(context,"获取完成");
					} else {
						//Toast.makeText(context, "失败："+Shell_ret.errorMsg, Toast.LENGTH_LONG).show();
						text1.setText("获取失败");
						text2.setText("点我查看错误信息");
					}
					SwitchLoadingState = false;
				}
			});
	}
	
	public static void  v2ray_MainRun(final Context context) { ((MainActivity) context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					GetStateAgent();
				}
			});
	}
	
	public static void log(String data) {
        Log.i("Log", data);
    }
	public void ControlInitialization() {
		//LinearLayout btn_call = (LinearLayout) findViewById(R.id.but1);
		progressBar = (SpinKitView)findViewById(R.id.spin_kit);
		imageView = (ImageView)findViewById(R.id.img);
		text1 = findViewById(R.id.text1);
		text2 = findViewById(R.id.text2);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.Main:
				if(SwitchLoadingState == true){
					Toast.makeText(this, "Loading...", Toast.LENGTH_LONG).show();
					return;
				}
				if(ModuleStatus == true){
					progressBar.setVisibility(View.VISIBLE);
					imageView.setVisibility(View.GONE);
					ModuleStatus = false;
					text1.setText("正在关闭");
					text2.setText("...");
					final String[] txt = new String[]{"cd "+path + "/",path + "/关闭.sh"};
					new Thread(new Runnable() {//创建子线程
							@Override
							public void run() {
								final ShellUtils.CommandResult ret = ShellUtils.execCommand(txt, true);
								Shell_ret = ret;
								v2ray.onfile(path + "/log.txt",ret.successMsg);
								v2ray_MainRun(MainActivity.this);
							}
						}).start();//启动子线程
					return;
				}
				progressBar.setVisibility(View.VISIBLE);
				imageView.setVisibility(View.GONE);
				text1.setText("尝试连接服务器");
				text2.setText("...");
				if (CurrentAgentInformation == null) {
					AlertDialog dialog=new AlertDialog.Builder(MainActivity.this)
						.setTitle("提示!")
						.setMessage("您未选择代理")
						.create();
					dialog.show();
					GetStateAgent();
                    return;
				}
				SwitchLoadingState = true;
				final String[] txt = new String[]{"cd "+path + "/",path + "/开启.sh"};
				final String[] txt_1 = new String[]{"cd "+path + "/",CurrentAgentInformation};
				new Thread(new Runnable() {//创建子线程
						@Override
						public void run() {
							final ShellUtils.CommandResult ret_1 = ShellUtils.execCommand(txt_1, true);
							Shell_ret = ret_1;
							final ShellUtils.CommandResult ret = ShellUtils.execCommand(txt, true);
							Shell_ret = ret;
							v2ray.onfile(path + "/log.txt",ret.successMsg);
							v2ray_MainRun(MainActivity.this);
						}
					}).start();//启动子线程
				break;
			case R.id.node:
				Intent intent = new Intent(MainActivity.this, NodeActivity.class);	//设置跳转到draw
				startActivity(intent);
				break;
			case R.id.http:
				DialogPlus dialog = DialogPlus.newDialog(this)
					//.setAdapter(animalAdapter)
					.setContentHolder(new ViewHolder(R.layout.activity_lay))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(DialogPlus dialog, View view) {
							//final EditText basicEt = (EditText) findViewById(R.id.basicEt);
							switch (view.getId()) {
								case R.id.button_send:
									dialog.dismiss();
									break;
								case R.id.button_send1:
									dialog.dismiss();
									break;
							}
						}
					})
					.setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
					//.setFooter(R.layout.activity_node)
				    .setFooter(R.layout.lay_footer2)
					.create();
				dialog.show();
				break;
			case R.id.AccessSubscribe:
				//final EditText basicEt = (EditText) findViewById(R.id.input_http);
				//订阅
				//DialogPlus diaLog = null;
				DialogPlus dialog_AccessSubscribe = DialogPlus.newDialog(this)
					//.setAdapter(animalAdapter)
					.setContentHolder(new ViewHolder(R.layout.activity_lay_AccessSubscribe))
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(DialogPlus dialog, View view) {
							//basicEt.setEnabled(!basicEt.isEnabled());
							switch (view.getId()) {
								case R.id.button_send:
									dialog.dismiss();
									break;
								case R.id.button_send1://确认
								    SwitchLoadingState = true;
								    final EditText basicEts = (EditText) dialog.findViewById(R.id.input_http1);
									String http = basicEts.getText().toString();
									final EditText basicEts_host = (EditText) dialog.findViewById(R.id.input_http2);
									String http_host = basicEts_host.getText().toString();
									v2ray.AccessSubscribe(MainActivity.this, http, http_host, path + "/");
									progressBar.setVisibility(View.VISIBLE);
									imageView.setVisibility(View.GONE);
									text1.setText("正在获取节点");
									text2.setText("...");
									dialog.dismiss();
									break;

							}
						}
					})
					.setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
					.setFooter(R.layout.lay_footer)
					.create();
				dialog_AccessSubscribe.show();
				final EditText basicEt = (EditText) dialog_AccessSubscribe.findViewById(R.id.input_http1);
				basicEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {//设置状态改变监听器
						@Override//状态改变
						public void onFocusChange(View v, boolean hasFocus) {
							if (basicEt.hasFocus() == false) {
							} else {
								basicEt.setError(null, null);//焦点聚焦时去除错误图标
							}
						}
					});

				break;
			case R.id.Release:
				Intent intent_Release = new Intent(MainActivity.this, ReleaseActivity.class);	//设置跳转到draw
				startActivity(intent_Release);
				break;
		}

	}
	//view.setVisibility(View.VISIBLE);可见
	//view.setVisibility(View.GONE);隐藏

} 

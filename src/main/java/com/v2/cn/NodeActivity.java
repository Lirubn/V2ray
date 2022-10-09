package com.v2.cn;

import android.app.Activity;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import android.widget.SimpleAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.view.Window;
import android.graphics.Color;
import android.content.Intent;

public class NodeActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_node);
		//沉浸导航栏
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
		List<String> arr = v2ray.getFilesAllName(MainActivity.path + "/节点选择/");
		final String[] code_v2 = new String[arr.size()];//地址
		final String[] mydata = new String[arr.size()];//名称
        for (int i=0;i < arr.size();i++) {
			code_v2[i] = arr.get(i);
            mydata[i] = arr.get(i).split(".sh")[0].split("节点选择/")[1];
		}
		ListView listView=(ListView)findViewById(R.id.list_item);//绑定列表的控件id
        //2.组织数据，把数据内容放到数组列表哈希表中
        ArrayList<HashMap<String, Object>> books = new ArrayList<HashMap<String, Object>>();//设置一个数组列表哈希表的对象
        for (int i = 0; i < mydata.length; i++) {//设置一个哈希表对象，用for循环把内容一个个存进数组中
            HashMap<String, Object> book = new HashMap<String, Object>();//设置哈希表对象
            book.put("tltle", mydata[i]);//（关键字，数据）的方式存放内容
            book.put("number", i);//（关键字，数据）的方式存放序号
            books.add(book);//把迭代对象book装进数组列表中
        }
        //3.把数据和Item组合,即实现adapter
        SimpleAdapter myaddlistadapter = new SimpleAdapter(//设置一个简单适配器，待会调用这个适配器
			this,//用的是这里的activity
			books,//用的是在这里的activity中的数组列表
			R.layout.item_code,//books对应的布局是item.XML
			new String[]{"tltle"},//对应books中的内容关键字
			new int[]{R.id.item_text});//对应布局中的两个布局，也是对应books中的关键字

        //4.显示Listview，刚刚设置了适配器，现在就是在调用适配器
        listView.setAdapter(myaddlistadapter);

        //5.监听操作listview，点击里面的内容就可以进行显示
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //MainActivity.log("你点的是第" + position + "行" + "    " + mydata[position]);
					MainActivity.CurrentAgentInformation = code_v2[position];
					MainActivity.GetStateAgent();
					v2ray.onfile(MainActivity.path+"/CurrentAgentInformation.txt",code_v2[position]);
					finish();
                }
            });
    }
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.itiem:
				MainActivity.GetStateAgent();
				finish();
				break;
		}
	}
}

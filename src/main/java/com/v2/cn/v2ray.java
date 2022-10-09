package com.v2.cn;

import com.v2.cn.ZipUtils;
import com.v2.cn.ShellUtils;
import android.content.Context;
import java.util.*; 
import java.io.*; 
import android.util.Log;
import android.app.Activity;
import android.widget.Toast;
import java.util.prefs.NodeChangeListener;



public class v2ray {

    public static void v2rayFileInit(Activity context, String files) {
		copyAssetsFile2Phone(context, "v2ray.zip");
		if (!fileIsExists(files + "/v2ray.zip") || fileIsExists(files + "/config.ini")) {
			if (fileIsExists(files + "/v2ray.zip")) {
				delete(files + "/v2ray.zip");
			}
            ShellUtils.CommandResult ret = ShellUtils.execCommand("chmod 777 -R " + MainActivity.path + "/", true);
			MainActivity.log(ret.successMsg);
			final String[] txt = new String[]{"cd "+MainActivity.path + "/",MainActivity.path + "/检测.sh"};
			MainActivity.Shell_ret = ShellUtils.execCommand(txt, true);
			onfile(MainActivity.path + "/log.txt",MainActivity.Shell_ret.successMsg);
			return;
		}
		//MainActivity.log(Integer.toString(exi.exists()));
		try {
			//解压ZIP压缩包
			ZipUtils.UnZipFolder(files + "/v2ray.zip", files);
			ShellUtils.CommandResult ret = ShellUtils.execCommand("chmod 777 -R " + MainActivity.path + "/", true);
			MainActivity.log(ret.successMsg);
			delete(files + "/v2ray.zip");
			final String[] txt = new String[]{"cd "+MainActivity.path + "/",MainActivity.path + "/检测.sh"};
			MainActivity.Shell_ret = ShellUtils.execCommand(txt, true);
			onfile(MainActivity.path + "/log.txt",MainActivity.Shell_ret.successMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static boolean fileIsExists(String strFile) {
        try {
            File f=new File(strFile);
            if(!f.exists()) {
	            return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
	
	public static boolean Detection() {
		ShellUtils.CommandResult ret = ShellUtils.execCommand("echo 666", true);
		if (ret.result == 0)
	        return true;
		else
			return false;
	}
	
	/**
     * 获取path文件夹下的所有文件
     * */
	public static List<String> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();
        if (files == null){Log.e("error","空目录");return null;}
        List<String> s = new ArrayList<>();
        for(int i =0;i<files.length;i++){
            s.add(files[i].getAbsolutePath());
        }
        return s;
    }
	
	public static boolean AccessSubscribe(final Context act, String URL_acc, String URL_host, final String file) {
		onfile(file+"url.txt",URL_acc);
		onfile(file+"hosts.txt",URL_host);
		final String[] txt = new String[]{"cd "+file + "/",file + "/一键订阅节点.sh"};
		new Thread(new Runnable() {//创建子线程
				@Override
				public void run() {
					final ShellUtils.CommandResult ret = ShellUtils.execCommand(txt, true);
				    MainActivity.Shell_ret = ret;
					onfile(file + "/log.txt",ret.errorMsg);
					MainActivity.updateUI_shell(act);
				}
			}).start();//启动子线程
		//ToastUtil.show(MainActivity.this,ret.successMsg);
		//ToastUtil.show(MainActivity.this,http_host);
		return false;
	}
	
	public static void onfile(String path, String st) {
        try {
            FileWriter fw = new FileWriter(path);
            fw.write(st);
            fw.close();
        } catch (IOException e) {}
    }
	/**
     * 将内容写入文件
     *
     * @author
     */
    private static void saveFile(Context context, String filePath, String content) {
        try {
            FileOutputStream outputStream = context.openFileOutput(filePath, Activity.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void v2ray_Main(){
		
	}
	
	public static boolean StringJudgment(String array, String trrs) {
		try {
            String[] text  = array.split(trrs);
			if (text.length == 1)
				return false;
			else
				return true;
        } catch (Exception e) {
			e.printStackTrace();
            return false;
		}
	}
    public static String readFile(String filename) {//带换行符读取
		StringBuilder fileContent = new StringBuilder("");
		File file = new File(filename);
		BufferedReader bufferedReader = null;
		String str = null;
		try {
			if (file.exists()) {
				bufferedReader = new BufferedReader(new FileReader(filename));
				while ((str = bufferedReader.readLine()) != null) {
					if (!fileContent.toString().equals("")) {
						fileContent.append("\r\n");
					}
					fileContent.append(str);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {}
		}
		return fileContent.toString();
	}

	/**
     *  从assets目录中复制整个文件夹内容,考贝到 /data/data/包名/files/目录中
     *  @param  activity  activity 使用CopyFiles类的Activity
     *  @param  filePath  String  文件路径,如：/assets/aa
     */
    public static void copyAssetsDir2Phone(Activity activity, String filePath) {
        try {
            String[] fileList = activity.getAssets().list(filePath);
            if (fileList.length > 0) {//如果是目录
                File file=new File(activity.getFilesDir().getAbsolutePath() + File.separator + filePath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName:fileList) {
                    filePath = filePath + File.separator + fileName;

                    copyAssetsDir2Phone(activity, filePath);

                    filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
                    Log.e("oldPath", filePath);
                }
            } else {//如果是文件
                InputStream inputStream=activity.getAssets().open(filePath);
                File file=new File(activity.getFilesDir().getAbsolutePath() + File.separator + filePath);
                Log.i("copyAssets2Phone", "file:" + file);
                if (!file.exists() || file.length() == 0) {
                    FileOutputStream fos=new FileOutputStream(file);
                    int len=-1;
                    byte[] buffer=new byte[1024];
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    inputStream.close();
                    fos.close();
                    //showToast(activity,"模型文件复制完毕");
                } else {
                    //showToast(activity,"模型文件已存在，无需复制");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文件从assets目录，考贝到 /data/data/包名/files/ 目录中。assets 目录中的文件，会不经压缩打包至APK包中，使用时还应从apk包中导出来
     * @param fileName 文件名,如aaa.txt
     */
    public static void copyAssetsFile2Phone(Activity activity, String fileName) {
        try {
            InputStream inputStream = activity.getAssets().open(fileName);
            //getFilesDir() 获得当前APP的安装路径 /data/data/包名/files 目录
            File file = new File(activity.getFilesDir().getAbsolutePath() + File.separator + fileName);
            if (!file.exists() || file.length() == 0) {
                FileOutputStream fos =new FileOutputStream(file);//如果文件不存在，FileOutputStream会自动创建文件
                int len=-1;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();//刷新缓存区
                inputStream.close();
                fos.close();
                //showToast(activity,"模型文件复制完毕");
            } else {
                //showToast(activity,"模型文件已存在，无需复制");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	/** 删除文件，可以是文件或文件夹
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            //Toast.makeText(getApplicationContext(), "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                //Toast.makeText(getApplicationContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            //Toast.makeText(getApplicationContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /** 删除目录及目录下的文件
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            //Toast.makeText(getApplicationContext(), "删除目录失败：" + filePath + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            } else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            //Toast.makeText(getApplicationContext(), "删除目录失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e("--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
            return true;
        } else {
            //Toast.makeText(getApplicationContext(), "删除目录：" + filePath + "失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}

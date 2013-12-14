package Utility;

import java.util.ArrayList;

public class queryParser {
	byte[] data;
	ArrayList<String> key;
	ArrayList<String> value;

	public queryParser() {
		key = new ArrayList<String>();
		value = new ArrayList<String>();
		data = null;
	}

	public queryParser(byte[] s) {
		key = new ArrayList<String>();
		value = new ArrayList<String>();
		String header = new String(s).split("\n\n")[0];
		int headerLen = header.length();
		data = new byte[s.length - headerLen - 2];
		System.arraycopy(s, headerLen + 2, data, 0, data.length);

		String[] headers = header.split("\n");
		String temp;
		String[] svalue;
		for (String header1 : headers) {
			svalue = header1.split(":");
			key.add(svalue[0]);
			temp = header1.substring(svalue[0].length() + 1);
			value.add(temp);
		}
	}

	public String get(String key) {
		for (int i = 0; i < this.key.size(); i++) {
			if (this.key.get(i).equals(key))
				return this.value.get(i);
		}
		return null;
	}

	public void setContent(byte[] bytes) {
//		System.out.println("set " + new String(bytes));
		data = bytes;
	}

	public byte[] getContent() {
//		System.out.println("get " + new String(data));
		return data;
	}

	public void add(String k, String v) {
		key.add(k);
		value.add(v);
	}

	public byte[] getBytes() {
		String outputS = "";
		byte[] output;
		for (int i = 0; i < key.size(); i++) {
			outputS += key.get(i) + ":" + value.get(i) + "\n";
		}
		outputS += "\n";
		output = new byte[outputS.getBytes().length + data.length];
		System.arraycopy(outputS.getBytes(), 0, output, 0, outputS.getBytes().length);
		System.arraycopy(data, 0, output, outputS.length(), data.length);

		return output;
	}
}

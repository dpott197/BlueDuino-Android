package com.aprilbrother.blueduino;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private final int SCAN_PERIOD = 60000;

    private final int REQUEST_ENABLE_BT = 1;
    private final int PERMISSION_REQUEST_FINE_LOCATION = 1;


    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mScanCallback;

    private Handler mHandler;

    private ArrayList<BluetoothDevice> devices;
    private ArrayList<String> device_macs;


    private ListView listView;
    private MyAdapter adapter;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.setDeviceList(devices);
            super.handleMessage(msg);
        }
    };
    private ScanCallback mScanCallback1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                    searchDevice();
                    showDeviceList();
                } else {
                    // Alert the user that this application requires the location permission to perform the scan.
                }
            }
        }
    }

    private void init() {
        adapter = new MyAdapter();
        mHandler = new Handler();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        devices = new ArrayList<BluetoothDevice>();
        device_macs = new ArrayList<String>();
    }

    /**
     * 查找蓝牙设备
     */
    private void searchDevice() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startScanning();
        }
    }

    private void showDeviceList() {
        listView = (ListView) findViewById(R.id.lv_blueduino_main);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent = new Intent(MainActivity.this, OperateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("device", devices.get(position));
                intent.putExtras(bundle);
                mBluetoothAdapter.stopLeScan(mScanCallback);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                startScanning();
                break;
            default:
                break;
        }
    }

    private void startScanning() {
        mScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.d(TAG, "BluetoothAdapter.LeScanCallback().onLeScan(...)");
                if (!device_macs.contains(device.getAddress())) {
                    if (device.getName() != null && device.getName().contains("ZeroBeacon"))
                        devices.add(device);
                    device_macs.add(device.getAddress());
                    handler.sendEmptyMessage(0);
                }
            }
        };

        mScanCallback1 = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Log.d(TAG, result.toString());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                for (ScanResult result : results) {
                    Log.d(TAG, result.toString());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.i(TAG, "errorCode: " + errorCode);
            }
        };

        mBluetoothAdapter.startLeScan(mScanCallback); // Replace with line below
        mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback1);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mScanCallback);
            }
        }, SCAN_PERIOD);
    }

    public class MyAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> myDevices = new ArrayList<BluetoothDevice>();

        @SuppressWarnings("unchecked")
        public void setDeviceList(ArrayList<BluetoothDevice> list) {
            if (list != null) {
                myDevices = (ArrayList<BluetoothDevice>) list.clone();
                notifyDataSetChanged();
            }
        }

        public void clearDeviceList() {
            if (myDevices != null) {
                myDevices.clear();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return myDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(MainActivity.this, R.layout.ble_list_item,
                        null);
                holder = new ViewHolder();
                holder.tv = (TextView) view
                        .findViewById(R.id.tv_ble_list_item_name);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.tv.setText(myDevices.get(position).getName());
            return view;
        }
    }

    class ViewHolder {
        TextView tv;
    }

}

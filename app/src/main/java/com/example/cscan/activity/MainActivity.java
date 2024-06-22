package com.example.cscan.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscan.R;

import com.example.cscan.adapter.DocumentAdapter;
import com.example.cscan.adapter.GroupAdapter;
import com.example.cscan.main_utils.Constant;
import com.example.cscan.models.ChangePasswordRequest;
import com.example.cscan.models.ChangePinRequest;
import com.example.cscan.models.DataTypes;
import com.example.cscan.models.Datas;
import com.example.cscan.models.Documents;
import com.example.cscan.models.TokenModel;
import com.example.cscan.service.CallbackService;
import com.example.cscan.service.DeleteCallback;
import com.example.cscan.service.IApiUserService;
import com.example.cscan.service.InsertDocuentCallBack;
import com.example.cscan.service.getListDataCallBack;
import com.example.cscan.service.getListDocumentCallBack;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText et_search;
    protected ImageView iv_drawer;
    protected ImageView iv_group_camera;
    private ListView lv_drawer;
    public RecyclerView rv_group;
    public LinearLayout ly_empty;
    private ImageView iv_folder;
    private ImageView iv_close_search;
    private ImageView iv_clear_txt;
    private static String current_group;
    private static String document_name_current;
    protected GroupAdapter groupAdapter;

    protected DocumentAdapter documentAdapter;
    private List<Documents> ListDocument;
    private List<Datas> ListData;
    public static MainActivity mainActivity;
    protected String current_mode;
    public SharedPreferences preferences;
    protected LinearLayoutManager layoutManager;
    protected TokenModel tokenModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFormWidgets();

    }

    @Override
    protected void onResume() {
        ListDocument = null;
        tokenModel = TokenModel.loadTokenModel(this);
        if (tokenModel != null) {
            String accessToken = tokenModel.getAccessToken();
            String refreshToken = tokenModel.getRefreshToken();

            Log.d("Token", "Loaded accessToken: " + accessToken);
            Log.d("Token", "Loaded refreshToken: " + refreshToken);
        } else {
            Log.e("Token", "TokenModel not found in SharedPreferences");
        }
        getAllDocument(Constant.user_current.getUserId(), tokenModel.getAccessToken(), new getListDocumentCallBack() {
            @Override
            public void onGetListDocumentCallBack(List<Documents> list) {
                ListDocument = list;
                for (Documents gp : list) {
                    System.out.println(gp);
                }
                System.out.println(ListDocument);
                setDocumentAdapter();
            }

        });

        super.onResume();
    }

    private void setDocumentAdapter() {

        layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        rv_group.setLayoutManager(layoutManager);
        documentAdapter = new DocumentAdapter(MainActivity.this, ListDocument);
        rv_group.setAdapter(documentAdapter);
        documentAdapter.notifyDataSetChanged();
    }

    private void getAllDocument(int userId, String token, getListDocumentCallBack callback) {

        IApiUserService.apiService.getAllDocument(userId, token)
                .enqueue(new Callback<List<Documents>>() {
                    @Override
                    public void onResponse(Call<List<Documents>> call, Response<List<Documents>> response) {
                        if (response.isSuccessful()) {
                            List<Documents> documents = response.body();
//                            for (Documents gp : documents) {
//                                System.out.println(gp);
//                            }
                            //  Toast.makeText(MainActivity.this, "Call thành công!", Toast.LENGTH_LONG).show();
                            // Process the list of images as needed
                            callback.onGetListDocumentCallBack(documents);
                        } else if (response.code() == 401) {
                            CallAPIRefreshToken(tokenModel);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Documents>> call, Throwable t) {
                        //Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                        Log.e("CALL API", t.getMessage());

                    }
                });
    }

    private void getAllData(int dataTypeId, getListDataCallBack callback) {

        IApiUserService.apiService.getAllData(dataTypeId)
                .enqueue(new Callback<List<Datas>>() {
                    @Override
                    public void onResponse(Call<List<Datas>> call, Response<List<Datas>> response) {
                        if (response.isSuccessful()) {
                            List<Datas> datas = response.body();
                            for (Datas gp : datas) {
                                System.out.println(gp);
                            }
                            // Toast.makeText(MainActivity.this, "Call thành công!", Toast.LENGTH_LONG).show();
                            // Process the list of images as needed
                            callback.onGetListDataCallBack(datas);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Datas>> call, Throwable t) {
                        //Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                        Log.e("CALL API", t.getMessage());

                    }
                });
    }

    private void getFormWidgets() {
//            drawer_ly = (DrawerLayout) findViewById(R.id.drawer_ly);
//            lv_drawer = (ListView) findViewById(R.id.lv_drawer);
        iv_drawer = (ImageView) findViewById(R.id.iv_drawer);
//            rl_search_bar = (RelativeLayout) findViewById(R.id.rl_search_bar);
        iv_close_search = (ImageView) findViewById(R.id.iv_close_search);

        iv_clear_txt = (ImageView) findViewById(R.id.iv_clear_txt);
//            tag_tabs = (TabLayout) findViewById(R.id.tag_tabs);
        rv_group = (RecyclerView) findViewById(R.id.rv_group);
//        ly_empty = (LinearLayout) findViewById(R.id.ly_empty);
//            tv_empty = (TextView) findViewById(R.id.tv_empty);
        iv_folder = findViewById(R.id.iv_folder);
        iv_group_camera = (ImageView) findViewById(R.id.iv_group_camera);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Nếu kích thước của văn bản là 0, "iv_clear_txt" sẽ không hiển thị
                if (i3 == 0) {
                    iv_clear_txt.setVisibility(View.INVISIBLE);

                } else if (i3 == 1) {
                    //Nếu kích thước của văn bản là 1, "iv_clear_txt" sẽ hiển thị
                    iv_clear_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            //Khi văn bản được nhập, hàm "filter" sẽ được gọi với văn bản đó là tham số.
            public void afterTextChanged(Editable editable) {
                if (ListDocument.size() > 0) {
                    filter(editable.toString());
                }
            }
        });
    }

    public void filter(String str) {
        ArrayList arrayList = new ArrayList();
        Iterator<Documents> it = ListDocument.iterator();
        while (it.hasNext()) {
            Documents next = it.next();

//            if (next.getDocumentName().toLowerCase().contains(str.toLowerCase())) {
//                arrayList.add(next);
//            }
            List<DataTypes> types = new ArrayList<>();
            IApiUserService.apiService.getAllDataType(next.getDocumentId())
                    .enqueue(new Callback<List<DataTypes>>() {
                        @Override
                        public void onResponse(Call<List<DataTypes>> call, Response<List<DataTypes>> response) {
                            if (response.isSuccessful()) {
                                List<DataTypes> Datatypes = response.body();

                                for (DataTypes gp : Datatypes) {
                                    types.add(gp);
                                }
                                // Toast.makeText(MainActivity.this, "Call thành công!", Toast.LENGTH_LONG).show();
                                // Process the list of images as needed
                            } else {
                                // Toast.makeText(CropDataTypeActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                                Log.e("API Response", "Request URL: " + call.request().url());
                                Log.e("API Response", "Response Code: " + response.code());
                                Log.e("API Response", "Response Message: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<DataTypes>> call, Throwable t) {
                            //Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                            Log.e("CALL API", t.getMessage());

                        }
                    });
            for (DataTypes d : types
            ) {
                getAllData(d.getDataTypeId(), new getListDataCallBack() {
                    @Override
                    public void onGetListDataCallBack(List<Datas> list) {
                        ListData = list;
                        for (Datas gp : list) {
                            if (gp.getDataValue().toLowerCase().trim().contains(str.trim().toLowerCase())) {
                                if (!arrayList.contains(next)) {
                                    arrayList.add(next);
                                }
                            }
                        }
                    }

                });
            }

        }
        documentAdapter.filterList(arrayList);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_clear_txt:
                et_search.setText("");
                iv_clear_txt.setVisibility(View.GONE);//ẩn text
                return;
            case R.id.iv_drawer:
                onClickInfo();
                return;
            case R.id.iv_group_camera:
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 1);
                return;
            case R.id.iv_folder:
                openNewFolderDialog();
                return;
            default:
                return;
        }
    }

    public void clickOnListItem(final Documents documents) {
        Constant.document_current = documents;
        document_name_current = Constant.document_current.getDocumentName();

        // Create an AlertDialog with an EditText for PIN input
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter PIN");

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredPIN = input.getText().toString();
                // Compare enteredPIN with the correct PIN
                String correctPIN = Constant.user_current.getPin(); // Replace with your actual correct PIN

                if (enteredPIN.equals(correctPIN)) {
                    Intent intent2 = new Intent(MainActivity.this, dataTypeActivity.class);
                    // Add extra data to the intent
                    // intent2.putExtra("current_group", current_group);
                    startActivity(intent2);
                } else {
                    // Show an error message
                    Toast.makeText(MainActivity.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void openNewFolderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.create_folder_dialog);
        dialog.getWindow().setLayout(-1, -2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        EditText et_folder_name = (EditText) dialog.findViewById(R.id.et_folder_name);


        ((TextView) dialog.findViewById(R.id.tv_create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String finalFolderName = et_folder_name.getText().toString().trim();
                if (!finalFolderName.isEmpty()) {
                    insertDocument(finalFolderName, new InsertDocuentCallBack() {
                        @Override
                        public void onInsertDocumentCallBack(Documents documents) {
                            Constant.document_current = documents;
                            DataTypes doc = new DataTypes("Doc", Constant.document_current.getDocumentId());
                            insertDataType(doc);
                            DataTypes pdf = new DataTypes("Pdf", Constant.document_current.getDocumentId());
                            insertDataType(pdf);
                            DataTypes Image = new DataTypes("Image", Constant.document_current.getDocumentId());
                            insertDataType(Image);
                            onResume();
                        }
                    });
                    dialog.dismiss();
                } else {
                    Toast.makeText(mainActivity, "Folder name is required", Toast.LENGTH_SHORT).show();
                }


            }
        });
        ((ImageView) dialog.findViewById(R.id.iv_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void insertDataType(DataTypes dataTypes) {

        IApiUserService.apiService.InsertDataType(dataTypes)
                .enqueue(new Callback<DataTypes>() {
                    @Override
                    public void onResponse(Call<DataTypes> call, Response<DataTypes> response) {
                        if (response.isSuccessful()) {
//                                DataTypes documents_current = response.body();
//                                System.out.println(documents_current);
                            //Toast.makeText(CropDocumentActivity.this, "Đăng kí thành công!", Toast.LENGTH_LONG).show();

                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<DataTypes> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED && checkSelfPermission("android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED) {
            Constant.inputType = "Group";
            Constant.IdentifyActivity = "ScannerActivity";
            startActivity(new Intent(MainActivity.this, ScannerActivity.class));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 2);
        }
    }

    public void onClickInfo() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.infor_dialog, (ViewGroup) null);
        TextView tvUserName = (TextView) inflate.findViewById(R.id.txtUserName);
        tvUserName.setText("NAME:  " + Constant.user_current.getUsername());
        TextView tvEmail = (TextView) inflate.findViewById(R.id.txtEmail);
        tvEmail.setText("EMAIL: " + Constant.user_current.getEmail());

        TextView tvPhoneNumber = (TextView) inflate.findViewById(R.id.txtPhoneNumber);
        tvPhoneNumber.setText("PHONE: " + Constant.user_current.getPhoneNumber());

        RelativeLayout rl_logOut = inflate.findViewById(R.id.rl_LogOut);
        RelativeLayout rl_changepassword = inflate.findViewById(R.id.rl_change_pass);
        RelativeLayout rl_changepin = inflate.findViewById(R.id.rl_change_pin);
        rl_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        rl_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.change_password_dialog, null);

                final EditText editTextOldPassword = view.findViewById(R.id.editTextOldPassword);
                final EditText editTextNewPassword = view.findViewById(R.id.editTextNewPassword);

                builder.setView(view)
                        .setTitle("Change Password")
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String oldPassword = editTextOldPassword.getText().toString();
                                String newPassword = editTextNewPassword.getText().toString();
                                // TODO: Handle password change logic
                                ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(Constant.user_current.getUsername(), oldPassword, newPassword);
                                IApiUserService.apiService.changePassword(changePasswordRequest)
                                        .enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    bottomSheetDialog.dismiss();
                                                    Intent intent = new Intent(MainActivity.this, Login.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                                                    Log.e("API Response", "Request URL: " + call.request().url());
                                                    Log.e("API Response", "Response Code: " + response.code());
                                                    Log.e("API Response", "Response Message: " + response.message());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                //Toast.makeText(GroupDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                Dialog dialog = builder.create();
                dialog.show();
            }
        });
        rl_changepin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.change_pin_dialog, null);

                final EditText editTextOldPin = view.findViewById(R.id.editTextOldPin);
                final EditText editTextNewPin = view.findViewById(R.id.editTextNewPin);

                builder.setView(view)
                        .setTitle("Change Password")
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String oldPin = editTextOldPin.getText().toString();
                                String newPin = editTextNewPin.getText().toString();
                                // TODO: Handle password change logic
                                ChangePinRequest changePinRequest = new ChangePinRequest(Constant.user_current.getUsername(), oldPin, newPin);
                                IApiUserService.apiService.changePin(changePinRequest)
                                        .enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "Pin changed", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    bottomSheetDialog.dismiss();
                                                    Intent intent = new Intent(MainActivity.this, Login.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                                                    Log.e("API Response", "Request URL: " + call.request().url());
                                                    Log.e("API Response", "Response Code: " + response.code());
                                                    Log.e("API Response", "Response Message: " + response.message());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                //Toast.makeText(GroupDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                Dialog dialog = builder.create();
                dialog.show();
            }

        });


        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }


    public void clickOnListMore(Documents documents) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View inflate = View.inflate(this, R.layout.document_more_dialog, (ViewGroup) null);
        final TextView tv_dialog_title = (TextView) inflate.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(documents.getDocumentName());
//        ((TextView) inflate.findViewById(R.id.tv_dialog_date)).setText(groupImage.getGroupDate());
        ((RelativeLayout) inflate.findViewById(R.id.rl_rename)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDocumentName(documents);
                bottomSheetDialog.dismiss();
            }
        });


        ((RelativeLayout) inflate.findViewById(R.id.rl_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("You want to delete this document?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteDocument(documents.getDocumentId(), new DeleteCallback() {
                            @Override
                            public void onDeleteCompleted() {
                                onResume();
                            }
                        }, MainActivity.this);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý logic khi người dùng chọn hủy
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(inflate);
        bottomSheetDialog.show();
    }

    private void updateDocumentName(Documents documents) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithRoundShape);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.update_group_name);
        dialog.getWindow().setLayout(-1, -2);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final EditText editText = (EditText) dialog.findViewById(R.id.et_group_name);
        editText.setText(documents.getDocumentName());
        editText.setSelection(editText.length());
        ((TextView) dialog.findViewById(R.id.tv_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals("") || Character.isDigit(editText.getText().toString().charAt(0))) {
                    Toast.makeText(MainActivity.this, "Please Enter Valid Document Name!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    upDateDocument(documents, editText.getText().toString(), new CallbackService() {
                        @Override
                        public void onCallbackService() {
                            onResume();
                        }
                    });
                    dialog.dismiss();
                }

            }
        });
        ((TextView) dialog.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        onResume();
        dialog.show();
    }

    private void upDateDocument(Documents documents, String document_name, CallbackService callback) {
        documents.setDocumentName(document_name);
        IApiUserService.apiService.updateDocument(documents)
                .enqueue(new Callback<Documents>() {
                    @Override
                    public void onResponse(Call<Documents> call, Response<Documents> response) {
                        if (response.isSuccessful()) {
                            Documents documents1 = response.body();
                            callback.onCallbackService();
                        } else {
                            Toast.makeText(MainActivity.this, "Name already exist", Toast.LENGTH_SHORT).show();
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Documents> call, Throwable t) {
                        //Toast.makeText(GroupDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void insertDocument(String folderName, InsertDocuentCallBack callback) {
        Documents documents = new Documents(folderName,
                Constant.user_current.getUserId(),
                Constant.getDateTime("yyyy-MM-dd  hh:mm a"));

        IApiUserService.apiService.insertDocument(documents)
                .enqueue(new Callback<Documents>() {
                    @Override
                    public void onResponse(Call<Documents> call, Response<Documents> response) {
                        if (response.isSuccessful()) {
                            Documents documents_current = response.body();
                            System.out.println(documents_current);
                            //Toast.makeText(CropDocumentActivity.this, "Đăng kí thành công!", Toast.LENGTH_LONG).show();
                            callback.onInsertDocumentCallBack(documents_current);
                        } else {
                            // Toast.makeText(CropDocumentActivity.this, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Documents> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void deleteDocument(int documentId, DeleteCallback callback, Activity activity) {
        IApiUserService.apiService.deleteDocument(documentId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            callback.onDeleteCompleted();
                            Toast.makeText(activity, "Delete succes!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(activity, "Đăng kí thất bại!", Toast.LENGTH_LONG).show();
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        //Toast.makeText(GroupDocumentActivity.this, "Call api error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void CallAPIRefreshToken(TokenModel tokenModel){
        IApiUserService.apiService.RefreshToken(tokenModel)
                .enqueue(new Callback<TokenModel>() {
                    @Override
                    public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                        if (response.isSuccessful()) {

                            // Get the token from the response headers
                            String accessToken = response.headers().get("AccessToken");
                            String refreshToken = response.headers().get("RefreshToken");

                            TokenModel tokenModel = new TokenModel();
                            tokenModel.setAccessToken(accessToken);
                            tokenModel.setRefreshToken(refreshToken);

                            tokenModel.saveTokenModel(MainActivity.this);

                        } else {
                            Log.e("API Response", "Request URL: " + call.request().url());
                            Log.e("API Response", "Response Code: " + response.code());
                            Log.e("API Response", "Response Message: " + response.message());

                        }
                    }

                    @Override
                    public void onFailure(Call<TokenModel> call, Throwable t) {

                    }
                });
    }

}
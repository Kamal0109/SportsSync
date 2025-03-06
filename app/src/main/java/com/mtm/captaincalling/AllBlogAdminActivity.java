package com.mtm.captaincalling;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mtm.captaincalling.Interface.ItemClickListener;

import java.util.Objects;

import io.paperdb.Paper;

public class AllBlogAdminActivity extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference blogRef;
    private FirebaseRecyclerAdapter<AllBlogs, AllBlogViewHolder> adapter;
    private ProgressBar progressBar,progressBar2;
    private ProgressDialog loadingBar;
    private ImageView imageView;
    private Dialog dialog;
    private EditText editText;
    private Button button;
    private RelativeLayout relativeLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_all_blog_admin, container, false);

        Paper.init(getContext());

        recyclerView = rootView.findViewById(R.id.recycler_blog_my1);

        progressBar = rootView.findViewById(R.id.mkbjvdtdstdtchcnjb1);

   //     imageView = rootView.findViewById(R.id.admin_youtube);

        relativeLayout = rootView.findViewById(R.id.admin_lyt);

     //   ImageView backBtn = rootView.findViewById(R.id.back_all_vlog_admin);


        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.login_dialog);
    //    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animations;

        editText = dialog.findViewById(R.id.ssdsdsdsd);
        progressBar2 = dialog.findViewById(R.id.njbhvgcgfx);
        button = dialog.findViewById(R.id.okbhvhvhaybtn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        loadingBar = new ProgressDialog(getContext());
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);



        if (!Paper.book().contains("Admin"))
        {
            dialog.show();
        }
        else
        {
            LoadData();
            relativeLayout.setVisibility(View.VISIBLE);
        }



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pass = editText.getText().toString();

                if (TextUtils.isEmpty(pass))
                {
                    editText.setError("Password is required");
                    editText.requestFocus();
                }
                else
                {
                        CheckPass(pass);


                }
            }
        });



        return rootView;
    }

    private void CheckPass(String pass) {
        progressBar2.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Admin");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if (Objects.equals(snapshot.child("Password").getValue(),pass))
                    {
                        editText.setTextColor(Color.GREEN);
                        Paper.book().write("Admin","Yes");

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LoadData();
                                dialog.dismiss();
                                relativeLayout.setVisibility(View.VISIBLE);
                            }
                        }, 1000);

                    }
                    else
                    {
                        progressBar2.setVisibility(View.INVISIBLE);
                        editText.setError("Wrong password");
                        editText.setTextColor(Color.RED);
                        editText.requestFocus();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    private boolean haveNetworkConnection() {
//        boolean haveConnectedWifi = false;
//        boolean haveConnectedMobile = false;
//
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
//                if (ni.isConnected())
//                    haveConnectedWifi = true;
//            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//                if (ni.isConnected())
//                    haveConnectedMobile = true;
//        }
//        return haveConnectedWifi || haveConnectedMobile;
//    }

    private void LoadData() {

        blogRef = FirebaseDatabase.getInstance().getReference("AllBlogs");

        FirebaseRecyclerOptions<AllBlogs> options =
                new FirebaseRecyclerOptions.Builder<AllBlogs>()
                        .setQuery(blogRef.orderByChild("Status").equalTo("off"), AllBlogs.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllBlogs, AllBlogViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllBlogViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull AllBlogs model) {

                holder.articleStatus.setVisibility(View.GONE);

                Glide.with(getContext()).load(model.getPicture()).into(holder.imageView);
                if (!model.getProfilePic().equals("null"))
                {
                    Glide.with(getContext()).load(model.getProfilePic()).into(holder.circleImageView);
                }
                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.datetime.setText(model.getDate());
                holder.name.setText(model.getName());

                if(Objects.equals(model.getIsPending(), "1")) {

                    holder.articleStatus.setVisibility(View.VISIBLE);

                    holder.articleStatus.setBackgroundColor(getResources().getColor(R.color.red));
                    holder.articleStatusText.setText("Pending");
                }

                if (model.getStatus().equals("off"))
                {
                    /*holder.status.setTextColor(Color.RED);
                    holder.status.setText("Pending");*/
                }
                else
                {
                    /*holder.status.setTextColor(Color.GREEN);
                    holder.status.setText("Public");*/
                }

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete",
                                        "Public",
                                        "Private",
                                        "Edit"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Choose option");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0)
                                {
                                    dialogInterface.dismiss();
                                    loadingBar.show();
                                    adapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            loadingBar.dismiss();
                                        }
                                    });
                                }

                                if (i==1)
                                {
                                    dialogInterface.dismiss();
                                    loadingBar.show();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllBlogs").child(Objects.requireNonNull(adapter.getRef(position).getKey()));

                                            ref.child("Status").setValue("on")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                            ref.child("isPending").setValue("0");
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loadingBar.dismiss();
                                        }
                                    });
                                }

                                if (i==2)
                                {
                                    dialogInterface.dismiss();
                                    loadingBar.show();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllBlogs").child(Objects.requireNonNull(adapter.getRef(position).getKey()));

                                            ref.child("Status").setValue("off")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                            ref.child("isPending").setValue("0");
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loadingBar.dismiss();
                                        }
                                    });
                                }
                                if (i==3)
                                {
                                    Intent intent = new Intent(getContext(),EditBlogActivity.class);
                                    intent.putExtra("Id",adapter.getRef(position).getKey());
                                    intent.putExtra("DesBlog",model.getDescription());
                                    intent.putExtra("PicBlog",model.getPicture());
                                    intent.putExtra("TitleBlog",model.getTitle());
                                    startActivity(intent);

                                }

                            }
                        });
                        builder.show();
                        return true;
                    }
                });

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent intent = new Intent(getContext(),BlogDetailsActivity.class);
                        intent.putExtra("DateBlog",model.getDate());
                        intent.putExtra("NameBlog",model.getName());
                        intent.putExtra("DesBlog",model.getDescription());
                        intent.putExtra("PicBlog",model.getPicture());
                        intent.putExtra("TitleBlog",model.getTitle());
                        intent.putExtra("PPicBlog",model.getProfilePic());
                        startActivity(intent);

                    }
                });

                progressBar.setVisibility(View.INVISIBLE);




            }

            @NonNull
            @Override
            public AllBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_model_private, parent,false);
                return  new AllBlogViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
package a6he.android.yzz.com.myrecycleview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    YzzRecycleView recyclerView;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                for (int i = 0; i <5 ; i++) {
                    list.add("新增"+i);
                }
            }
            recyclerView.complete();
            Log.e("======","==dd========"+list.size());
            handler.removeMessages(1);
        }
    };
    private List<String> list;
    private RecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (YzzRecycleView) findViewById(R.id.recycle_view);
        //RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManger(manager,3);
        adapter = new RecycleAdapter(this);
        list = new ArrayList<>();
        for (int i = 0; i <20 ; i++) {
            list.add("=="+i+"==");
        }
        adapter.setList(list);
        recyclerView.setNeedFootFresh(true);
        recyclerView.setNeedHeadFresh(true);
        recyclerView.setMAdapter(adapter);

        recyclerView.setOnLoadMoreListener(new YzzRecycleView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("======","==dd========"+list.size());
                handler.sendEmptyMessageDelayed(1,3000);
            }
        });
    }
}

package a6he.android.yzz.com.myrecycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Created by yzz on 2017/2/26 0026.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyHolder> {

    private List<String> list;
    private Context context;

    public RecycleAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder holder = new MyHolder(LayoutInflater.from(context).inflate(R.layout.item,null));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        holder.textView.setText(list.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"=="+position+"==",Toast.LENGTH_LONG).show();
            }
        });
    }

    public List<String> getList(){
        return list;
    }


    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    class MyHolder extends YzzRecycleView.ViewHolder{

        TextView textView;
        public MyHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_tv);
        }
    }
}

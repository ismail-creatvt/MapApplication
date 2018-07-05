package com.creatvt.ismail.mapapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import java.util.List;

public class TrackTableAdapter extends AbstractTableAdapter<ColumnHeader,RowHeader,Cell> {

    public TrackTableAdapter(Context context) {
        super(context);
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    private class CellViewHolder extends AbstractViewHolder {
        TextView cellData;
        CellViewHolder(View view) {
            super(view);
            this.cellData = view.findViewById(R.id.cell_data);
        }
    }

    @Override
    public int getCellItemViewType(int position) {
        return 0;
    }

    @Override
    public AbstractViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        return new CellViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_layout,parent,false));
    }

    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, Object cellItemModel, int columnPosition, int rowPosition) {
        ((CellViewHolder)holder).cellData.setText(((Cell) cellItemModel).getData());
    }

    private class ColumnViewHolder extends AbstractViewHolder {
        TextView txtTitle;
        ColumnViewHolder(View view) {
            super(view);
            this.txtTitle = view.findViewById(R.id.column_data);
        }
    }

    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        return new ColumnViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.column_header_layout,parent,false));
    }

    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, Object columnHeaderItemModel, int columnPosition) {
        ((ColumnViewHolder) holder).txtTitle.setText(((ColumnHeader) columnHeaderItemModel).getTitle());
    }
    private class RowViewHolder extends AbstractViewHolder {
        TextView txtId;
        public RowViewHolder(View view) {
            super(view);
            this.txtId = view.findViewById(R.id.row_data);
        }
    }

    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        return new RowViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_header_layout,parent,false));
    }

    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Object rowHeaderItemModel, int rowPosition) {
        ((RowViewHolder) holder).txtId.setText(((RowHeader) rowHeaderItemModel).getId());
    }

    @Override
    public View onCreateCornerView() {
        return new View(mContext);
    }


}

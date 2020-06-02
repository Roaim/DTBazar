package app.roaim.dtbazar.ui;

import android.view.View;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ListItemClickListener<T> {
    void onItemClick(@Nullable T item, @NotNull View itemView, boolean isLongClick);
}

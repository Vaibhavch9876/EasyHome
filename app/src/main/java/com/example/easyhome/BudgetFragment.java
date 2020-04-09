package com.example.easyhome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.easyhome.data.MyDBHandler;
import com.example.easyhome.model.Transaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static java.lang.StrictMath.max;
import static java.lang.StrictMath.min;


public class BudgetFragment extends Fragment {


    private static final int RESET_SWIPE = 1010;
    private long mLastClickTime;
    private boolean isswiped = false;
    private MyDBHandler db;
    private RecyclerView transactionIncomeRecyclerView;
    private TransactionAdapter transactionIncomeAdapter;
    private RecyclerView transactionExpenseRecyclerView;
    private TransactionAdapter transactionExpenseAdapter;
    private List<Transaction> incomeTransactions = new ArrayList<>();
    private List<Transaction> expenseTransactions = new ArrayList<>();

    private TextView netIncome;
    private TextView netExpense;
    private TextView budgetSavings;
    private TextView budgetIncome;
    private TextView budgetExpense;
    private TextView budgetSpentPercent;
    private TextView budgetPercentText;
    private ProgressBar budgetProgressbar;

    private RecyclerViewTouchListener incomeListener;
    private RecyclerViewTouchListener expenseListener;
    private boolean swipable = true;

    public BudgetFragment() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = new MyDBHandler(getContext());

        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        final BottomNavigationView mBottomNavigationView = view.findViewById(R.id.bottom_navigation_view);


        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setReverseLayout(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        linearLayoutManager2.setReverseLayout(true);

        transactionIncomeRecyclerView = view.findViewById(R.id.income_budget_recycler_view);
        transactionIncomeRecyclerView.setLayoutManager(linearLayoutManager1);

        transactionExpenseRecyclerView = view.findViewById(R.id.expense_budget_recycler_view);
        transactionExpenseRecyclerView.setLayoutManager(linearLayoutManager2);

        netIncome = view.findViewById(R.id.net_income_textview);
        netExpense = view.findViewById(R.id.net_expense_textview);
        budgetIncome = view.findViewById(R.id.budget_income);
        budgetExpense = view.findViewById(R.id.budget_expense);
        budgetSavings = view.findViewById(R.id.budget_savings);
        budgetSpentPercent = view.findViewById(R.id.budget_spent_percent);
        budgetPercentText = view.findViewById(R.id.budget_percent_text);
        budgetProgressbar = view.findViewById(R.id.budget_progressbar);


        mBottomNavigationView.getMenu().getItem(0).setCheckable(false);
        mBottomNavigationView.getMenu().getItem(1).setCheckable(false);
        mBottomNavigationView.getMenu().getItem(2).setCheckable(false);


        incomeListener = new RecyclerViewTouchListener(getContext(), transactionIncomeRecyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (isswiped)
                    return;

                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {

                    Transaction item = transactionIncomeAdapter.getTransaction(position);

                    Intent showTransactionIntent = new Intent(getContext(), TransactionShowActivity.class);
                    showTransactionIntent.putExtra("Transaction", item);
                    startActivity(showTransactionIntent);
                }
                mLastClickTime = SystemClock.elapsedRealtime();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
        expenseListener = new RecyclerViewTouchListener(getContext(), transactionExpenseRecyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (isswiped)
                    return;
                Log.e("Not", "Swiped");
                if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                    Transaction item = transactionExpenseAdapter.getTransaction(position);

                    Intent showTransactionIntent = new Intent(getContext(), TransactionShowActivity.class);
                    showTransactionIntent.putExtra("Transaction", item);
                    startActivity(showTransactionIntent);
                }
                mLastClickTime = SystemClock.elapsedRealtime();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return true;
                }
                switch (item.getItemId()) {
                    case R.id.add_income:
                        Intent incomeIntent = new Intent(getContext(), IncomeActivity.class);
                        startActivity(incomeIntent);
                        break;
                    case R.id.add_expense:
                        Intent expenseIntent = new Intent(getContext(), ExpenseActivity.class);
                        startActivity(expenseIntent);
                        break;
                    case R.id.show_transactions:
                        Intent allTransactionIntent = new Intent(getContext(), AllTransactionActivity.class);
                        startActivity(allTransactionIntent);
                        break;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        swipable = true;

        incomeTransactions = db.getIncomeTransactions();
        expenseTransactions = db.getExpenseTransactions();

        transactionIncomeAdapter = new TransactionAdapter(incomeTransactions, getContext());
        transactionIncomeRecyclerView.setAdapter(transactionIncomeAdapter);
        transactionExpenseAdapter = new TransactionAdapter(expenseTransactions, getContext());
        transactionExpenseRecyclerView.setAdapter(transactionExpenseAdapter);


        transactionExpenseRecyclerView.addOnItemTouchListener(expenseListener);
        transactionIncomeRecyclerView.addOnItemTouchListener(incomeListener);


        setUpItemTouchHelperIncome();
        setUpItemTouchHelperExpense();
        setUpAnimationDecoratorHelper();


        setAddOns();
    }

    private void setAddOns() {

        double net_income = 0, net_expense = 0;
        for (Transaction transaction : incomeTransactions)
            net_income += transaction.getTransactionAmount();
        for (Transaction transaction : expenseTransactions)
            net_expense += transaction.getTransactionAmount();

        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(2);
        df.setMaximumIntegerDigits(10);

        netIncome.setText(df.format(net_income));
        netExpense.setText(df.format(net_expense));

        budgetIncome.setText(df.format(net_income));
        budgetExpense.setText(df.format(net_expense));
        budgetSavings.setText(df.format(max(0, net_income - net_expense)));

        budgetProgressbar.setMax((int) net_income);
        budgetProgressbar.setProgress((int) min(net_income, net_expense));


        double spent_percent = 1000000;
        if (net_income != 0) {
            spent_percent = net_expense * 100 / net_income;
            spent_percent = (int) (spent_percent * 100);
            spent_percent = spent_percent / 100;

            if (spent_percent < 10000)
                budgetSpentPercent.setText(df.format(spent_percent) + " % ");
            else
                budgetSpentPercent.setText(" > 10000 %");
        }
        else {
            if (net_expense == 0)
                budgetSpentPercent.setText("0.00 % ");
            else
                budgetSpentPercent.setText(" > 10000 % ");
        }

        if (spent_percent > 100) {
            budgetPercentText.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorAccentRed));
            budgetSpentPercent.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorAccentRed));
            budgetPercentText.setTextColor(getResources().getColor(R.color.colorWhite));
            budgetSpentPercent.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            budgetPercentText.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorAccentGreen));
            budgetSpentPercent.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.colorAccentGreen));
            budgetPercentText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            budgetSpentPercent.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

    }

    private void setUpItemTouchHelperIncome() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(getContext(), R.drawable.delete_icon);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) 10;
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!swipable)
                    return 0;
                if (transactionIncomeAdapter.getPendings() != 0 || transactionExpenseAdapter.getPendings() != 0)
                    return 0;
                isswiped = true;
                int position = viewHolder.getAdapterPosition();
                TransactionAdapter transactionAdapter = (TransactionAdapter) recyclerView.getAdapter();
                if (transactionAdapter.undoOn && transactionAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                isswiped = false;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                isswiped = true;
                int swipedPosition = viewHolder.getAdapterPosition();
                TransactionAdapter adapter = (TransactionAdapter) transactionIncomeRecyclerView.getAdapter();
                boolean undoOn = adapter.undoOn;
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback2 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;
            TransactionAdapter adapter;

            private void init() {
                background = new ColorDrawable(Color.GREEN);
                xMark = ContextCompat.getDrawable(getContext(), R.drawable.edit_icon);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) 20;
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!swipable)
                    return 0;
                if (transactionIncomeAdapter.getPendings() != 0 || transactionExpenseAdapter.getPendings() != 0)
                    return 0;
                isswiped = true;
                adapter = (TransactionAdapter) recyclerView.getAdapter();
                isswiped = false;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                swipable = false;
                isswiped = true;
                int position = viewHolder.getAdapterPosition();
                Transaction item = adapter.getTransaction(position);

                Intent editIncomeIntent = new Intent(getContext(), IncomeActivity.class);
                editIncomeIntent.putExtra("Transaction", (Serializable) item);
                editIncomeIntent.putExtra("Title", "Edit Income");

                startActivityForResult(editIncomeIntent, RESET_SWIPE);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getLeft() + (int) dX, itemView.getTop(), itemView.getLeft(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getLeft() + xMarkMargin;
                int xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper mItemTouchHelper1 = new ItemTouchHelper(simpleItemTouchCallback1);
        ItemTouchHelper mItemTouchHelper2 = new ItemTouchHelper(simpleItemTouchCallback2);

        mItemTouchHelper1.attachToRecyclerView(transactionIncomeRecyclerView);
        mItemTouchHelper2.attachToRecyclerView(transactionIncomeRecyclerView);

    }

    private void setUpItemTouchHelperExpense() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(getContext(), R.drawable.delete_icon);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) 10;
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!swipable)
                        return 0;
                if (transactionIncomeAdapter.getPendings() != 0 || transactionExpenseAdapter.getPendings() != 0)
                    return 0;
                isswiped = true;
                int position = viewHolder.getAdapterPosition();
                TransactionAdapter transactionAdapter = (TransactionAdapter) recyclerView.getAdapter();
                if (transactionAdapter.undoOn && transactionAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                isswiped = false;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                isswiped = true;
                int swipedPosition = viewHolder.getAdapterPosition();
                TransactionAdapter adapter = (TransactionAdapter) transactionExpenseRecyclerView.getAdapter();
                boolean undoOn = adapter.undoOn;
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback2 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;
            TransactionAdapter adapter;

            private void init() {
                background = new ColorDrawable(Color.GREEN);
                xMark = ContextCompat.getDrawable(getContext(), R.drawable.edit_icon);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) 20;
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!swipable)
                    return 0;
                if (transactionIncomeAdapter.getPendings() != 0 || transactionExpenseAdapter.getPendings() != 0)
                    return 0;
                isswiped = true;
                adapter = (TransactionAdapter) recyclerView.getAdapter();
                isswiped = false;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                swipable = false;
                isswiped = true;

                int position = viewHolder.getAdapterPosition();
                Transaction item = adapter.getTransaction(position);

                Intent editExpenseIntent = new Intent(getContext(), ExpenseActivity.class);
                editExpenseIntent.putExtra("Transaction", (Serializable) item);
                editExpenseIntent.putExtra("Title", "Edit Expense");

                startActivityForResult(editExpenseIntent, RESET_SWIPE);

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;

                if (viewHolder.getAdapterPosition() == -1) {
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getLeft() + (int) dX, itemView.getTop(), itemView.getLeft(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getLeft() + xMarkMargin;
                int xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper mItemTouchHelper1 = new ItemTouchHelper(simpleItemTouchCallback1);
        ItemTouchHelper mItemTouchHelper2 = new ItemTouchHelper(simpleItemTouchCallback2);

        mItemTouchHelper1.attachToRecyclerView(transactionExpenseRecyclerView);
        mItemTouchHelper2.attachToRecyclerView(transactionExpenseRecyclerView);

    }

    private void setUpAnimationDecoratorHelper() {

        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {

            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                if (parent.getItemAnimator().isRunning()) {

                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    int left = 0;
                    int right = parent.getWidth();

                    int top = 0;
                    int bottom = 0;

                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        };

        transactionIncomeRecyclerView.addItemDecoration(itemDecoration);
        transactionExpenseRecyclerView.addItemDecoration(itemDecoration);

    }

    private void resetisswiped() {
        isswiped = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESET_SWIPE) {
            if (resultCode == RESULT_OK){
                resetisswiped();
            }
        }

    }

    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

        private static final long PENDING_REMOVAL_TIMEOUT = 2000;
        private List<Transaction> transactionList;
        private List<Transaction> transactionPendingList;
        public boolean undoOn = true;
        private MyDBHandler db;

        private Handler handler = new Handler();
        HashMap<Transaction, Runnable> pendingRunnables = new HashMap<>();


        public Transaction getTransaction(int position) {
            return transactionList.get(position);
        }


        public class TransactionViewHolder extends RecyclerView.ViewHolder {

            public TextView amt_Text, catText, dateText;
            public View hide_line;
            public LinearLayout undoButton;
            public LinearLayout normalLayout;

            public TransactionViewHolder(@NonNull View itemView) {
                super(itemView);

                amt_Text = itemView.findViewById(R.id.income_amount_display);
                catText = itemView.findViewById(R.id.income_category_display);
                dateText = itemView.findViewById(R.id.income_date_display);
                hide_line = itemView.findViewById(R.id.income_hide_line);
                undoButton = itemView.findViewById(R.id.undo_layout_income);
                normalLayout = itemView.findViewById(R.id.income_normal_layout);

            }
        }

        public TransactionAdapter(List<Transaction> transactionList, Context context) {
            this.transactionList = transactionList;
            db = new MyDBHandler(context);
            this.transactionPendingList = new ArrayList<>();
        }

        @Override
        public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_layout, parent, false);

            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

            final Transaction item = transactionList.get(position);

            if (transactionPendingList.contains(item)) {

                holder.normalLayout.setVisibility(View.GONE);
                holder.undoButton.setVisibility(View.VISIBLE);

                holder.undoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                        pendingRunnables.remove(item);


                        if (pendingRemovalRunnable != null)
                            handler.removeCallbacks(pendingRemovalRunnable);
                        transactionPendingList.remove(item);

                        notifyItemChanged(transactionList.indexOf(item));
                        resetisswiped();

                    }
                });
            } else {
                holder.normalLayout.setVisibility(View.VISIBLE);

                double amt = transactionList.get(position).getTransactionAmount();

                DecimalFormat df = new DecimalFormat("#");
                df.setMaximumFractionDigits(2);
                df.setMaximumIntegerDigits(10);

                holder.amt_Text.setText(df.format(amt));

                holder.catText.setText(transactionList.get(position).getTransactionCategory());
                holder.dateText.setText(transactionList.get(position).getTransactionDate());

                if (position == 0)
                    holder.hide_line.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        public void pendingRemoval(int position) {
            final Transaction item = transactionList.get(position);
            if (!transactionPendingList.contains(item)) {
                transactionPendingList.add(item);

                notifyItemChanged(position);

                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {

                        db.deleteTransaction(item);
                        Log.e("Delete ", "Runnable");
                        remove(transactionList.indexOf(item));
                        pendingRunnables.clear();
                        setAddOns();
                        resetisswiped();
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(item, pendingRemovalRunnable);
            }
        }

        public int getPendings() {
            return pendingRunnables.size();
        }

        public void remove(int position) {
            Transaction item = transactionList.get(position);
            if (transactionPendingList.contains(item)) {
                transactionPendingList.remove(item);
            }
            if (transactionList.contains(item)) {
                transactionList.remove(position);
                notifyItemRemoved(position);
            }
        }

        public boolean isPendingRemoval(int position) {
            Transaction item = transactionList.get(position);
            return transactionPendingList.contains(item);
        }

    }

}




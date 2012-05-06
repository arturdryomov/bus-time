package app.android.bustime.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


public class DashboardLayout extends ViewGroup
{
	private static final int UNEVEN_GRID_PENALTY_MULTIPLIER = 10;

	private int maximumChildWidth = 0;
	private int maximumChildHeight = 0;

	public DashboardLayout(Context context) {
		super(context, null);
	}

	public DashboardLayout(Context context, AttributeSet attributes) {
		super(context, attributes, 0);
	}

	public DashboardLayout(Context context, AttributeSet attributess, int style) {
		super(context, attributess, style);
	}

	@Override
	protected void onMeasure(int measureSpecWidth, int measureSpecHeight) {
		maximumChildWidth = 0;
		maximumChildHeight = 0;

		// Measure once to find the maximum child size

		int childMeasureSpecWidth = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(measureSpecWidth),
			MeasureSpec.AT_MOST);
		int childMeasureSpecHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(measureSpecWidth),
			MeasureSpec.AT_MOST);

		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);

			if (child.getVisibility() == GONE) {
				continue;
			}

			child.measure(childMeasureSpecWidth, childMeasureSpecHeight);

			maximumChildWidth = Math.max(maximumChildWidth, child.getMeasuredWidth());
			maximumChildHeight = Math.max(maximumChildHeight, child.getMeasuredHeight());
		}

		// Measure again for each child to be exactly the same size

		childMeasureSpecWidth = MeasureSpec.makeMeasureSpec(maximumChildWidth, MeasureSpec.EXACTLY);
		childMeasureSpecHeight = MeasureSpec.makeMeasureSpec(maximumChildHeight, MeasureSpec.EXACTLY);

		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);

			if (child.getVisibility() == GONE) {
				continue;
			}

			child.measure(childMeasureSpecWidth, childMeasureSpecHeight);
		}

		setMeasuredDimension(resolveSize(maximumChildWidth, measureSpecWidth),
			resolveSize(maximumChildHeight, measureSpecHeight));
	}

	@Override
	protected void onLayout(boolean changed, int leftPosition, int topPosition, int rightPosition,
		int bottomPosition) {
		int width = rightPosition - leftPosition;
		int height = bottomPosition - topPosition;

		int visibleChildrenCount = calculateVisibleChildrenCount();

		if (visibleChildrenCount == 0) {
			return;
		}

		// Calculate what number of rows and columns will optimize for even horizontal and
		// vertical whitespace between items. Start with a 1 x N grid, then try 2 x N, and so on

		int bestSpaceDifference = Integer.MAX_VALUE;
		int spaceDifference;
		int horizonatlItemSpace = 0;
		int verticalItemSpace = 0;
		int columnsCount = 1;
		int rowsCount;

		while (true) {
			rowsCount = (visibleChildrenCount - 1) / columnsCount + 1;

			horizonatlItemSpace = ((width - maximumChildWidth * columnsCount) / (columnsCount + 1));
			verticalItemSpace = ((height - maximumChildHeight * rowsCount) / (rowsCount + 1));

			spaceDifference = Math.abs(verticalItemSpace - horizonatlItemSpace);
			if (rowsCount * columnsCount != visibleChildrenCount) {
				spaceDifference *= UNEVEN_GRID_PENALTY_MULTIPLIER;
			}

			if (spaceDifference < bestSpaceDifference) {
				// Found a better whitespace squareness/ratio
				bestSpaceDifference = spaceDifference;

				// If we found a better whitespace squareness and there's only 1 row, this is
				// the best we can do
				if (rowsCount == 1) {
					break;
				}
			}
			else {
				// This is a worse whitespace ratio, use the previous value of cols and exit
				--columnsCount;
				rowsCount = (visibleChildrenCount - 1) / columnsCount + 1;
				horizonatlItemSpace = ((width - maximumChildWidth * columnsCount) / (columnsCount + 1));
				verticalItemSpace = ((height - maximumChildHeight * rowsCount) / (rowsCount + 1));
				break;
			}

			++columnsCount;
		}

		// Lay out children based on calculated best-fit number of rows and cols

		// If we chose a layout that has negative horizontal or vertical space, force it to zero
		horizonatlItemSpace = Math.max(0, horizonatlItemSpace);
		verticalItemSpace = Math.max(0, verticalItemSpace);

		// Re-use width/height variables to be child width/height
		width = (width - horizonatlItemSpace * (columnsCount + 1)) / columnsCount;
		height = (height - verticalItemSpace * (rowsCount + 1)) / rowsCount;

		int left, top;
		int col, row;
		int visibleIndex = 0;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}

			row = visibleIndex / columnsCount;
			col = visibleIndex % columnsCount;

			left = horizonatlItemSpace * (col + 1) + width * col;
			top = verticalItemSpace * (row + 1) + height * row;

			child.layout(left, top, (horizonatlItemSpace == 0 && col == columnsCount - 1) ? rightPosition
				: (left + width), (verticalItemSpace == 0 && row == rowsCount - 1) ? bottomPosition
				: (top + height));
			++visibleIndex;
		}
	}

	private int calculateVisibleChildrenCount() {
		int visibleChildrenCount = 0;

		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);

			if (child.getVisibility() == GONE) {
				continue;
			}

			visibleChildrenCount++;
		}

		return visibleChildrenCount;
	}
}

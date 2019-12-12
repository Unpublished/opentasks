/*
 * Copyright 2019 dmfs GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dmfs.tasks;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.viewpager.widget.ViewPager;


@RunWith(AndroidJUnit4.class)
public class TaskListActivityTest
{
    public static final int RETRIES = 70;
    private static final String TAG = TaskListActivityTest.class.getSimpleName();
    private int i;


    @Test
    public void testIllegalStateException()
    {
        try (ActivityScenario<TaskListActivity> scenario = ActivityScenario.launch(TaskListActivity.class))
        {
            scenario.onActivity(activity -> {
                ViewPager viewPager = activity.mViewPager;
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
                {
                    private int lastPos;


                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                    {

                    }


                    @Override
                    public void onPageSelected(int position)
                    {
                        Log.d(TAG, "onPageSelected() called with: position = [" + position + "]");
                    }


                    @Override
                    public void onPageScrollStateChanged(int state)
                    {
                        Log.d(TAG, "onPageScrollStateChanged() i=" + i);
                        Log.d(TAG, "onPageScrollStateChanged() called with: state = [" + state + "]");
                        if (state != ViewPager.SCROLL_STATE_IDLE || i > RETRIES)
                        {
                            return;
                        }
                        switch (lastPos)
                        {
                            case 0:
                                lastPos = 4;
                                break;
                            case 5:
                                lastPos = 0;
                                break;
                            default:
                                lastPos = 5;
                        }
                        activity.runOnUiThread(() -> {
                            TaskListFragment fragment = (TaskListFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            int count = fragment.mExpandableListView.getCount();
                            long[] groups = new long[count];
                            Arrays.setAll(groups, value -> value);
                            fragment.mExpandableListView.expandGroups(groups);
                            viewPager.setCurrentItem(lastPos, true);
                        });
                        i++;
                    }
                });
                activity.runOnUiThread(() -> viewPager.setCurrentItem((viewPager.getCurrentItem() + 1) % viewPager.getAdapter().getCount(), true));

            });
            while (i < RETRIES)
            {
                InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            }
        }
    }
}
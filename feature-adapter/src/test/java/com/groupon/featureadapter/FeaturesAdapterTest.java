/*
 * Copyright (c) 2017, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.groupon.featureadapter;

import static android.support.v7.widget.RecyclerView.INVALID_TYPE;
import static android.support.v7.widget.RecyclerView.ViewHolder;
import static com.groupon.featureadapter.TestUtils.fixAdapterForTesting;
import static java.util.Arrays.asList;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class FeaturesAdapterTest {

  @Test
  public void registerFeatures_should_assignValidViewTypesToAdapterViewTypeDelegates()
      throws Exception {
    //GIVEN
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate = new StubAdapterViewTypeDelegate();
    List<FeatureController<String>> featureControllers =
        asList(new StubFeatureController<>(asList(stubAdapterViewTypeDelegate)));

    //WHEN
    FeaturesAdapter<String> featuresAdapter = new FeaturesAdapter<>(featureControllers);
    fixAdapterForTesting(featuresAdapter);

    //THEN
    assertThat(stubAdapterViewTypeDelegate.getViewType(), not(INVALID_TYPE));
  }

  @Test
  public void getItemCount_should_returnAllItems() throws Exception {
    //GIVEN
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate = new StubAdapterViewTypeDelegate();
    List<ViewItem> items = new ArrayList<>();
    List<FeatureController<String>> featureControllers =
        asList(new StubFeatureController<>(asList(stubAdapterViewTypeDelegate), items));
    FeaturesAdapter<String> featuresAdapter = new FeaturesAdapter<>(featureControllers);
    fixAdapterForTesting(featuresAdapter);

    items.add(new ViewItem<>("a0", stubAdapterViewTypeDelegate));
    items.add(new ViewItem<>("a1", stubAdapterViewTypeDelegate));
    featuresAdapter.updateFeatureItems("a");

    //WHEN
    final int itemCount = featuresAdapter.getItemCount();

    //THEN
    assertThat(itemCount, is(2));
  }

  @Test
  public void
      getItemViewType_should_returnCorrectViewTypeOfAdapterViewTypeDelegateForAGivenPosition()
          throws Exception {
    //GIVEN
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate0 = new StubAdapterViewTypeDelegate();
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate1 = new StubAdapterViewTypeDelegate();
    List<ViewItem> items = new ArrayList<>();
    List<FeatureController<String>> featureControllers =
        asList(
            new StubFeatureController<>(
                asList(stubAdapterViewTypeDelegate0, stubAdapterViewTypeDelegate1), items));
    FeaturesAdapter<String> featuresAdapter = new FeaturesAdapter<>(featureControllers);
    fixAdapterForTesting(featuresAdapter);

    items.add(new ViewItem<>("a0", stubAdapterViewTypeDelegate0));
    items.add(new ViewItem<>("a1", stubAdapterViewTypeDelegate1));
    featuresAdapter.updateFeatureItems("a");

    //WHEN
    final int viewType0 = featuresAdapter.getItemViewType(0);
    final int viewType1 = featuresAdapter.getItemViewType(1);

    //THEN
    assertThat(viewType0, is(stubAdapterViewTypeDelegate0.getViewType()));
    assertThat(viewType1, is(stubAdapterViewTypeDelegate1.getViewType()));
  }

  @Test
  public void updateFeatureItems_should_updateOldItems() throws Exception {
    //GIVEN
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate0 = new StubAdapterViewTypeDelegate();
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate1 = new StubAdapterViewTypeDelegate();
    List<ViewItem> items = new ArrayList<>();
    List<FeatureController<String>> featureControllers =
        Arrays.asList(
            new StubFeatureController<>(
                asList(stubAdapterViewTypeDelegate0, stubAdapterViewTypeDelegate1), items));
    FeaturesAdapter<String> featuresAdapter = new FeaturesAdapter<>(featureControllers);
    fixAdapterForTesting(featuresAdapter);

    items.add(new ViewItem<>("a0", stubAdapterViewTypeDelegate0));
    items.add(new ViewItem<>("a1", stubAdapterViewTypeDelegate1));
    featuresAdapter.updateFeatureItems("a");
    items.clear();
    items.add(new ViewItem<>("a0", stubAdapterViewTypeDelegate0));
    items.add(new ViewItem<>("a1", stubAdapterViewTypeDelegate0));
    featuresAdapter.updateFeatureItems("a");

    //WHEN
    final int viewType0 = featuresAdapter.getItemViewType(0);
    final int viewType1 = featuresAdapter.getItemViewType(1);

    //THEN
    assertThat(viewType0, is(stubAdapterViewTypeDelegate0.getViewType()));
    assertThat(viewType1, is(stubAdapterViewTypeDelegate0.getViewType()));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void onCreateViewHolder_should_useTheRightAdapterViewTypeDelegateForAGivenPosition()
      throws Exception {
    //GIVEN
    final LinearLayout parent = new LinearLayout(createMock(Context.class));
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate0 =
        createNiceMock(StubAdapterViewTypeDelegate.class);
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate1 =
        createNiceMock(StubAdapterViewTypeDelegate.class);
    expect(stubAdapterViewTypeDelegate0.getViewType()).andReturn(0).anyTimes();
    expect(stubAdapterViewTypeDelegate1.getViewType()).andReturn(1).anyTimes();
    expect(stubAdapterViewTypeDelegate1.createViewHolder(anyObject(ViewGroup.class)))
        .andReturn(new ViewHolder(parent) {});
    replay(stubAdapterViewTypeDelegate0, stubAdapterViewTypeDelegate1);

    List<ViewItem> items = new ArrayList<>();
    List<FeatureController<String>> featureControllers =
        Arrays.asList(
            new StubFeatureController<>(
                asList(stubAdapterViewTypeDelegate0, stubAdapterViewTypeDelegate1), items));
    FeaturesAdapter<String> featuresAdapter = new FeaturesAdapter<>(featureControllers);
    fixAdapterForTesting(featuresAdapter);

    items.add(new ViewItem("a0", stubAdapterViewTypeDelegate0));
    items.add(new ViewItem("a1", stubAdapterViewTypeDelegate1));
    featuresAdapter.updateFeatureItems("a");

    //WHEN
    featuresAdapter.onCreateViewHolder(parent, 1);

    //THEN
    verify(stubAdapterViewTypeDelegate0, stubAdapterViewTypeDelegate1);
  }

  @Test
  public void onBindViewHolder_should_useTheRightAdapterViewTypeDelegateForAGivenPosition()
      throws Exception {
    //GIVEN
    final LinearLayout parent = new LinearLayout(createMock(Context.class));
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate0 =
        createNiceMock(StubAdapterViewTypeDelegate.class);
    StubAdapterViewTypeDelegate stubAdapterViewTypeDelegate1 =
        createNiceMock(StubAdapterViewTypeDelegate.class);
    expect(stubAdapterViewTypeDelegate0.getViewType()).andReturn(0).anyTimes();
    expect(stubAdapterViewTypeDelegate1.getViewType()).andReturn(1).anyTimes();
    stubAdapterViewTypeDelegate1.bindViewHolder(anyObject(ViewHolder.class), eq("a1"));
    replay(stubAdapterViewTypeDelegate0, stubAdapterViewTypeDelegate1);

    List<ViewItem> items = new ArrayList<>();
    List<FeatureController<String>> featureControllers =
        Arrays.asList(
            new StubFeatureController<>(
                asList(stubAdapterViewTypeDelegate0, stubAdapterViewTypeDelegate1), items));
    FeaturesAdapter<String> featuresAdapter = new FeaturesAdapter<>(featureControllers);
    fixAdapterForTesting(featuresAdapter);

    items.add(new ViewItem<>("a0", stubAdapterViewTypeDelegate0));
    items.add(new ViewItem<>("a1", stubAdapterViewTypeDelegate1));
    featuresAdapter.updateFeatureItems("a");

    //WHEN
    featuresAdapter.bindViewHolder(new ViewHolder(parent) {}, 1);

    //THEN
    verify(stubAdapterViewTypeDelegate0, stubAdapterViewTypeDelegate1);
  }
}

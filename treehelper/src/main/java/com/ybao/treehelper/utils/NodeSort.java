
/**
 * Copyright 2015 Pengyuan-Jiang
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Author：Ybao on 2015/11/15 17:07
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.treehelper.utils;

import com.ybao.treehelper.model.Node;

import java.util.Comparator;

public class NodeSort implements Comparator<Node> {
    @Override
    public int compare(Node lhs, Node rhs) {
        if (lhs.getpId() != rhs.getpId()) {
            return 0;
        }
        int lid = lhs.getSortId();
        int rid = rhs.getSortId();
        if (rid > lid) {
            return -1;
        } else if (rid == lid) {
            return 0;
        } else {
            return 1;
        }
    }
}

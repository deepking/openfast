/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
 */
package org.openfast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openfast.template.Group;

public class TemplateDictionary implements Dictionary {
    protected Map<Group, Map<QName, ScalarValue>> table = new HashMap<Group, Map<QName, ScalarValue>>();

    @Override
    public ScalarValue lookup(Group template, QName key, QName applicationType) {
        if (!table.containsKey(template)) {
            return ScalarValue.UNDEFINED;
        }

        if ((table.get(template)).containsKey(key)) {
            return table.get(template).get(key);
        }

        return ScalarValue.UNDEFINED;
    }

    @Override
    public void reset() {
        table.clear();
    }

    @Override
    public void store(Group group, QName applicationType, QName key, ScalarValue valueToEncode) {
        if (!table.containsKey(group)) {
            table.put(group, new HashMap<QName, ScalarValue>());
        }

        table.get(group).put(key, valueToEncode);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Iterator<Group> templateIterator = table.keySet().iterator();
        while (templateIterator.hasNext()) {
            Object template = templateIterator.next();
            builder.append("Dictionary: Template=" + template.toString());
            Map<QName, ScalarValue> templateMap = table.get(template);
            Iterator<QName> keyIterator = templateMap.keySet().iterator();
            while (keyIterator.hasNext()) {
                Object key = keyIterator.next();
                builder.append(key).append("=").append(templateMap.get(key)).append("\n");
            }
        }
        return builder.toString();
    }
}

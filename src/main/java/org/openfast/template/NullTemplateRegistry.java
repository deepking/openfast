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
package org.openfast.template;

import java.util.Collections;
import java.util.Iterator;

import org.openfast.QName;

final class NullTemplateRegistry implements TemplateRegistry {
    @Override
    public void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {
    }

    @Override
    public MessageTemplate get(int templateId) {
        return null;
    }

    @Override
    public MessageTemplate get(String templateName) {
        return null;
    }

    public int getTemplateId(String templateName) {
        return 0;
    }

    public int getTemplateId(MessageTemplate template) {
        return 0;
    }

    @Override
    public MessageTemplate[] getTemplates() {
        return new MessageTemplate[] {};
    }

    @Override
    public boolean isRegistered(String templateName) {
        return false;
    }

    @Override
    public boolean isRegistered(int templateId) {
        return false;
    }

    @Override
    public boolean isRegistered(MessageTemplate template) {
        return false;
    }

    @Override
    public void register(int templateId, MessageTemplate template) {
    }

    @Override
    public void remove(String name) {
    }

    @Override
    public void remove(MessageTemplate template) {
    }

    @Override
    public void remove(int id) {
    }

    public void add(MessageTemplate template) {
    }

    @Override
    public void define(MessageTemplate template) {
    }

    public MessageTemplate getTemplate(String name) {
        return null;
    }

    public MessageTemplate getTemplate(QName name) {
        return null;
    }

    public MessageTemplate getTemplate(int id) {
        return null;
    }

    public boolean hasTemplate(String name) {
        return false;
    }

    public boolean hasTemplate(QName name) {
        return false;
    }

    public boolean hasTemplate(int id) {
        return false;
    }

    public boolean isDefined(MessageTemplate template) {
        return false;
    }

    public MessageTemplate[] toArray() {
        return null;
    }

    @Override
    public MessageTemplate get(QName name) {
        return null;
    }

    @Override
    public int getId(String name) {
        return 0;
    }

    @Override
    public int getId(MessageTemplate template) {
        return 0;
    }

    @Override
    public boolean isDefined(QName name) {
        return false;
    }

    @Override
    public boolean isDefined(String name) {
        return false;
    }

    @Override
    public void register(int templateId, QName name) {
    }

    @Override
    public void register(int templateId, String name) {
    }

    @Override
    public void removeTemplateRegisteredListener(
            TemplateRegisteredListener templateRegisteredListener) {
    }

    @Override
    public int getId(QName name) {
        return 0;
    }

    @Override
    public boolean isRegistered(QName name) {
        return false;
    }

    @Override
    public void remove(QName name) {
    }

    @Override
    public void registerAll(TemplateRegistry registry) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<QName> nameIterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<MessageTemplate> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }
}
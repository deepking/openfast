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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openfast.codec.FastDecoder;
import org.openfast.logging.FastMessageLogger.Direction;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegisteredListener;
import org.openfast.template.TemplateRegistry;

public class MessageInputStream implements MessageStream {
    private InputStream in;
    private FastDecoder decoder;
    private Context context;
    private Map<MessageTemplate, MessageHandler> templateHandlers = Collections.emptyMap();
    private List<MessageHandler> handlers = Collections.emptyList();
    private MessageBlockReader blockReader = MessageBlockReader.NULL;

    public MessageInputStream(InputStream inputStream) {
        this(inputStream, new Context());
    }

    public MessageInputStream(InputStream inputStream, Context context) {
        this.in = inputStream;
        this.context = context;
        this.decoder = new FastDecoder(context, in);
    }

    /**
     * @throws java.io.EOFException
     * @return the next message in the stream
     */
    public Message readMessage() {
        if (context.isTraceEnabled())
            context.startTrace();
        boolean keepReading = blockReader.readBlock(in);
        if (!keepReading)
            return null;
        Message message = decoder.readMessage();
        if (message == null) {
            return null;
        }
        getContext().getLogger().log(message, ByteUtil.EMPTY, Direction.INBOUND);
        blockReader.messageRead(in, message);
        if (!handlers.isEmpty()) {
            for (int i = 0; i < handlers.size(); i++) {
                ((MessageHandler) handlers.get(i)).handleMessage(message, context, decoder);
            }
        }
        if (templateHandlers.containsKey(message.getTemplate())) {
            MessageHandler handler = (MessageHandler) templateHandlers.get(message.getTemplate());
            handler.handleMessage(message, context, decoder);
            return readMessage();
        }
        return message;
    }

    public void registerTemplate(int templateId, MessageTemplate template) {
        context.registerTemplate(templateId, template);
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getUnderlyingStream() {
        return in;
    }

    public void addMessageHandler(MessageTemplate template, MessageHandler handler) {
        if (templateHandlers == Collections.EMPTY_MAP) {
            templateHandlers = new HashMap<MessageTemplate, MessageHandler>();
        }
        templateHandlers.put(template, handler);
    }

    public void addMessageHandler(MessageHandler handler) {
        if (handlers == Collections.EMPTY_LIST) {
            handlers = new ArrayList<MessageHandler>(4);
        }
        handlers.add(handler);
    }

    public void setTemplateRegistry(TemplateRegistry registry) {
        context.setTemplateRegistry(registry);
    }

    public TemplateRegistry getTemplateRegistry() {
        return context.getTemplateRegistry();
    }

    public void addTemplateRegisteredListener(TemplateRegisteredListener templateRegisteredListener) {}

    public void reset() {
        decoder.reset();
    }

    public Context getContext() {
        return context;
    }

    public void setBlockReader(MessageBlockReader messageBlockReader) {
        this.blockReader = messageBlockReader;
    }
}

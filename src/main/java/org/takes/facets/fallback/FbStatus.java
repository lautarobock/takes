/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.facets.fallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Condition;
import org.takes.tk.TkFixed;

/**
 * Fallback on status code that equals to the provided value.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.13
 */
@EqualsAndHashCode(callSuper = true)
public final class FbStatus extends FbWrap {

    /**
     * Ctor.
     * @param code HTTP status code
     * @param response Response
     * @since 0.14
     */
    public FbStatus(final int code, final Response response) {
        this(code, new TkFixed(response));
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param take Take
     */
    public FbStatus(final int code, final Take take) {
        this(
            code,
            new Fallback() {
                @Override
                public Iterator<Response> route(final RqFallback req)
                    throws IOException {
                    return Collections.singleton(take.act(req)).iterator();
                }
            }
        );
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param fallback Fallback
     */
    public FbStatus(final int code, final Fallback fallback) {
        this(
            new Condition<Integer>() {
                @Override
                public boolean fits(final Integer status) {
                    return code == status;
                }
            },
            fallback
        );
    }

    /**
     * Ctor.
     * @param check Check
     * @param fallback Fallback
     */
    public FbStatus(final Condition<Integer> check, final Fallback fallback) {
        super(
            new Fallback() {
                @Override
                public Iterator<Response> route(final RqFallback req)
                    throws IOException {
                    final Collection<Response> rsp = new ArrayList<Response>(1);
                    if (check.fits(req.code())) {
                        final Iterator<Response> iter = fallback.route(req);
                        if (iter.hasNext()) {
                            rsp.add(iter.next());
                        }
                    }
                    return rsp.iterator();
                }
            }
        );
    }
}

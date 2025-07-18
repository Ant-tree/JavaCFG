/*
 The MIT License

 Copyright (c) 2004-2021 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package joptsimple;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * A specification for an option that does not accept arguments.
 *
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class NoArgumentOptionSpec extends AbstractOptionSpec<Void> {
    NoArgumentOptionSpec( String option ) {
        this( singletonList( option ), "" );
    }

    NoArgumentOptionSpec( List<String> options, String description ) {
        super( options, description );
    }

    @Override
    void handleOption( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions,
        String detectedArgument ) {

        detectedOptions.add( this );
    }

    @Override
    public boolean acceptsArguments() {
        return false;
    }

    @Override
    public boolean requiresArgument() {
        return false;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public boolean isProcessorSwitch() {
        return false;
    }

    @Override
    public String argumentDescription() {
        return "";
    }

    @Override
    public String argumentTypeIndicator() {
        return "";
    }

    @Override
    public String shortTypeIndicator() {
        return "";
    }

    @Override
    public String shortTypeIndicatorStyled(String prefix) {
        return "";
    }

    @Override
    public boolean hasShortTypeIndicator() {
        return false;
    }

    @Override
    public Optional<ValueConverter<?>> argumentConverter() {
        return Optional.empty();
    }

    @Override
    protected Void convert( String argument ) {
        return null;
    }

    @Override
    public List<Void> defaultValues() {
        return emptyList();
    }

    @Override
    public String defaultValuesStyled(String prefix) {
        return "";
    }
}

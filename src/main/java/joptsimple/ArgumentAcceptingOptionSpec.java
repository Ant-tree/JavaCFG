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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static joptsimple.internal.Reflection.findConverter;

/**
 * <p>Specification of an option that accepts an argument.</p>
 *
 * <p>Instances are returned from {@link OptionSpecBuilder} methods to allow the formation of parser directives as
 * sentences in a "fluent interface" language. For example:</p>
 *
 * <pre>
 *   <code>
 *   OptionParser parser = new OptionParser();
 *   parser.accepts( "c" ).withRequiredArg().<strong>ofType( Integer.class )</strong>;
 *   </code>
 * </pre>
 *
 * <p>If no methods are invoked on an instance of this class, then that instance's option will treat its argument as
 * a {@link String}.</p>
 *
 * @param <V> represents the type of the arguments this option accepts
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
public abstract class ArgumentAcceptingOptionSpec<V> extends AbstractOptionSpec<V> {
    private static final char NIL_VALUE_SEPARATOR = '\u0000';

    private final boolean isProcessorSwitch;
    private final boolean argumentRequired;
    private final List<V> defaultValues = new ArrayList<>();

    private boolean optionRequired;
    private ValueConverter<V> converter;
    private String argumentDescription = "";
    private String argumentDescriptionKoKr = "";
    private String valueSeparator = String.valueOf( NIL_VALUE_SEPARATOR );

    ArgumentAcceptingOptionSpec( String option, boolean argumentRequired, boolean isProcessorSwitch ) {
        super( option );

        this.argumentRequired = argumentRequired;
        this.isProcessorSwitch = isProcessorSwitch;
    }

    ArgumentAcceptingOptionSpec( List<String> options, boolean argumentRequired, boolean isProcessorSwitch, String description ) {
        super( options, description );

        this.argumentRequired = argumentRequired;
        this.isProcessorSwitch = isProcessorSwitch;
    }

    /**
     * <p>Specifies a type to which arguments of this spec's option are to be converted.</p>
     *
     * <p>JOpt Simple accepts types that have either:</p>
     *
     * <ol>
     *   <li>a public static method called {@code valueOf} which accepts a single argument of type {@link String}
     *   and whose return type is the same as the class on which the method is declared.  The {@code java.lang}
     *   primitive wrapper classes have such methods.</li>
     *
     *   <li>a public constructor which accepts a single argument of type {@link String}.</li>
     * </ol>
     *
     * <p>This class converts arguments using those methods in that order; that is, {@code valueOf} would be invoked
     * before a one-{@link String}-arg constructor would.</p>
     *
     * <p>Invoking this method will trump any previous calls to this method or to
     * {@link #withValuesConvertedBy(ValueConverter)}.</p>
     *
     * @param <T> represents the runtime class of the desired option argument type
     * @param argumentType desired type of arguments to this spec's option
     * @return self, so that the caller can add clauses to the fluent interface sentence
     * @throws NullPointerException if the type is {@code null}
     * @throws IllegalArgumentException if the type does not have the standard conversion methods
     */
    public final <T> ArgumentAcceptingOptionSpec<T> ofType( Class<T> argumentType ) {
        return withValuesConvertedBy( findConverter( argumentType ) );
    }

    /**
     * <p>Specifies a converter to use to translate arguments of this spec's option into Java objects.  This is useful
     * when converting to types that do not have the requisite factory method or constructor for
     * {@link #ofType(Class)}.</p>
     *
     * <p>Invoking this method will trump any previous calls to this method or to {@link #ofType(Class)}.
     *
     * @param <T> represents the runtime class of the desired option argument type
     * @param aConverter the converter to use
     * @return self, so that the caller can add clauses to the fluent interface sentence
     * @throws NullPointerException if the converter is {@code null}
     */
    @SuppressWarnings( "unchecked" )
    public final <T> ArgumentAcceptingOptionSpec<T> withValuesConvertedBy( ValueConverter<T> aConverter ) {
        if ( aConverter == null )
            throw new NullPointerException( "illegal null converter" );

        converter = (ValueConverter<V>) aConverter;
        return (ArgumentAcceptingOptionSpec<T>) this;
    }

    /**
     * <p>Specifies a description for the argument of the option that this spec represents.  This description is used
     * when generating help information about the parser.</p>
     *
     * @param description describes the nature of the argument of this spec's option
     * @return self, so that the caller can add clauses to the fluent interface sentence
     */
    public final ArgumentAcceptingOptionSpec<V> describedAs( String description ) {
        argumentDescription = description;
        return this;
    }

    /**
     * <p>Specifies a value separator for the argument of the option that this spec represents.  This allows a single
     * option argument to represent multiple values for the option.  For example:</p>
     *
     * <pre>
     *   <code>
     *   parser.accepts( "z" ).withRequiredArg()
     *       .<strong>withValuesSeparatedBy( ',' )</strong>;
     *   OptionSet options = parser.parse( new String[] { "-z", "foo,bar,baz", "-z",
     *       "fizz", "-z", "buzz" } );
     *   </code>
     * </pre>
     *
     * <p>Then <code>options.valuesOf( "z" )</code> would yield the list {@code [foo, bar, baz, fizz, buzz]}.</p>
     *
     * <p>You cannot use Unicode U+0000 as the separator.</p>
     *
     * @param separator a character separator
     * @return self, so that the caller can add clauses to the fluent interface sentence
     * @throws IllegalArgumentException if the separator is Unicode U+0000
     */
    public final ArgumentAcceptingOptionSpec<V> withValuesSeparatedBy( char separator ) {
        if ( separator == NIL_VALUE_SEPARATOR )
            throw new IllegalArgumentException( "cannot use U+0000 as separator" );

        valueSeparator = String.valueOf( separator );
        return this;
    }

    /**
     * <p>Specifies a value separator for the argument of the option that this spec represents.  This allows a single
     * option argument to represent multiple values for the option.  For example:</p>
     *
     * <pre>
     *   <code>
     *   parser.accepts( "z" ).withRequiredArg()
     *       .<strong>withValuesSeparatedBy( ":::" )</strong>;
     *   OptionSet options = parser.parse( new String[] { "-z", "foo:::bar:::baz", "-z",
     *       "fizz", "-z", "buzz" } );
     *   </code>
     * </pre>
     *
     * <p>Then <code>options.valuesOf( "z" )</code> would yield the list {@code [foo, bar, baz, fizz, buzz]}.</p>
     *
     * <p>You cannot use Unicode U+0000 in the separator.</p>
     *
     * @param separator a string separator
     * @return self, so that the caller can add clauses to the fluent interface sentence
     * @throws IllegalArgumentException if the separator contains Unicode U+0000
     */
    public final ArgumentAcceptingOptionSpec<V> withValuesSeparatedBy( String separator ) {
        if ( separator.indexOf( NIL_VALUE_SEPARATOR ) != -1 )
            throw new IllegalArgumentException( "cannot use U+0000 in separator" );

        valueSeparator = separator;
        return this;
    }

    /**
     * Specifies a set of default values for the argument of the option that this spec represents.
     *
     * @param value the first in the set of default argument values for this spec's option
     * @param values the (optional) remainder of the set of default argument values for this spec's option
     * @return self, so that the caller can add clauses to the fluent interface sentence
     * @throws NullPointerException if {@code value}, {@code values}, or any elements of {@code values} are
     * {@code null}
     */
    @SafeVarargs
    public final ArgumentAcceptingOptionSpec<V> defaultsTo( V value, V... values ) {
        addDefaultValue( value );
        defaultsTo( values );

        return this;
    }

    /**
     * Specifies a set of default values for the argument of the option that this spec represents.
     *
     * @param values the set of default argument values for this spec's option
     * @return self, so that the caller can add clauses to the fluent interface sentence
     * @throws NullPointerException if {@code values} or any elements of {@code values} are {@code null}
     */
    public ArgumentAcceptingOptionSpec<V> defaultsTo( V[] values ) {
        for ( V each : values )
            addDefaultValue( each );

        return this;
    }

    /**
     * Marks this option as required. An {@link OptionException} will be thrown when
     * {@link OptionParser#parse(String...)} is called, if an option is marked as required and not specified
     * on the command line.
     *
     * @return self, so that the caller can add clauses to the fluent interface sentence
     */
    public ArgumentAcceptingOptionSpec<V> required() {
        optionRequired = true;
        return this;
    }

    @Override
    public boolean isRequired() {
        return optionRequired;
    }

    @Override
    public boolean isProcessorSwitch() {
        return isProcessorSwitch;
    }

    private void addDefaultValue(V value ) {
        requireNonNull( value );
        defaultValues.add( value );
    }

    @Override
    final void handleOption( OptionParser parser, ArgumentList arguments, OptionSet detectedOptions,
        String detectedArgument ) {

        if ( detectedArgument == null )
            detectOptionArgument( parser, arguments, detectedOptions );
        else
            addArguments( detectedOptions, detectedArgument );
    }

    protected void addArguments( OptionSet detectedOptions, String detectedArgument ) {
        StringTokenizer lexer = new StringTokenizer( detectedArgument, valueSeparator );
        if ( !lexer.hasMoreTokens() )
            detectedOptions.addWithArgument( this, detectedArgument );
        else {
            while ( lexer.hasMoreTokens() )
                detectedOptions.addWithArgument( this, lexer.nextToken() );
        }
    }

    protected abstract void detectOptionArgument( OptionParser parser, ArgumentList arguments,
        OptionSet detectedOptions );

    @Override
    protected final V convert( String argument ) {
        return convertWith( converter, argument );
    }

    protected boolean canConvertArgument( String argument ) {
        StringTokenizer lexer = new StringTokenizer( argument, valueSeparator );

        try {
            while ( lexer.hasMoreTokens() )
                convert( lexer.nextToken() );
            return true;
        } catch ( OptionException ignored ) {
            return false;
        }
    }

    protected boolean isArgumentOfNumberType() {
        return converter != null && Number.class.isAssignableFrom( converter.valueType() );
    }

    @Override
    public boolean acceptsArguments() {
        return true;
    }

    @Override
    public boolean requiresArgument() {
        return argumentRequired;
    }

    @Override
    public String argumentDescription() {
        return argumentDescription;
    }

    @Override
    public String argumentTypeIndicator() {
        return argumentTypeIndicatorFrom( converter );
    }

    @Override
    public String shortTypeIndicator() {
        String typeIndicator = argumentTypeIndicator();
        if (typeIndicator == null || typeIndicator.isEmpty()) {
            return typeIndicator;
        }
        String[] typeTokens = typeIndicator.split("\\.");
        if (typeTokens.length != 0) {
            return typeTokens[typeTokens.length -1];
        }
        return typeIndicator;
    }

    @Override
    public String shortTypeIndicatorStyled(String prefix) {
        String shortType = shortTypeIndicator();
        return (shortType != null && !shortType.isEmpty())
                ? prefix + "Type : [" + shortType + "]"
                : "";
    }

    @Override
    public boolean hasShortTypeIndicator() {
        return shortTypeIndicator() != null && !shortTypeIndicator().isEmpty();
    }

    @Override
    public List<V> defaultValues() {
        return unmodifiableList( defaultValues );
    }

    @Override
    public String defaultValuesStyled(String prefix) {
        return defaultValues().isEmpty()
                ? ""
                : prefix + "(default: " + defaultValues() + ")";
    }

    @Override
    public Optional<ValueConverter<?>> argumentConverter() {
        return Optional.ofNullable( converter );
    }

    @Override
    public boolean equals( Object that ) {
        if ( !super.equals( that ) )
            return false;

        ArgumentAcceptingOptionSpec<?> other = (ArgumentAcceptingOptionSpec<?>) that;
        return requiresArgument() == other.requiresArgument();
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ ( argumentRequired ? 0 : 1 );
    }
}

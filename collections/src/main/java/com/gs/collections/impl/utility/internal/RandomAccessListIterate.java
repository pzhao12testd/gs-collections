/*
 * Copyright 2015 Goldman Sachs.
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

package com.gs.collections.impl.utility.internal;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.Function3;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.ByteFunction;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.IntObjectToIntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.LongObjectToLongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableByteCollection;
import com.gs.collections.api.collection.primitive.MutableCharCollection;
import com.gs.collections.api.collection.primitive.MutableDoubleCollection;
import com.gs.collections.api.collection.primitive.MutableFloatCollection;
import com.gs.collections.api.collection.primitive.MutableIntCollection;
import com.gs.collections.api.collection.primitive.MutableLongCollection;
import com.gs.collections.api.collection.primitive.MutableShortCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.MutableBooleanList;
import com.gs.collections.api.list.primitive.MutableByteList;
import com.gs.collections.api.list.primitive.MutableCharList;
import com.gs.collections.api.list.primitive.MutableDoubleList;
import com.gs.collections.api.list.primitive.MutableFloatList;
import com.gs.collections.api.list.primitive.MutableIntList;
import com.gs.collections.api.list.primitive.MutableLongList;
import com.gs.collections.api.list.primitive.MutableShortList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.primitive.ObjectDoubleMap;
import com.gs.collections.api.map.primitive.ObjectLongMap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Functions0;
import com.gs.collections.impl.block.procedure.MutatingAggregationProcedure;
import com.gs.collections.impl.block.procedure.NonMutatingAggregationProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.list.mutable.primitive.ByteArrayList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.list.mutable.primitive.DoubleArrayList;
import com.gs.collections.impl.list.mutable.primitive.FloatArrayList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.primitive.LongArrayList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.mutable.primitive.ObjectDoubleHashMap;
import com.gs.collections.impl.map.mutable.primitive.ObjectLongHashMap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.partition.list.PartitionFastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.Iterate;
import com.gs.collections.impl.utility.ListIterate;

/**
 * The ListIterate class provides a few of the methods from the Smalltalk Collection Protocol for use with ArrayLists.
 * This includes do:, select:, reject:, collect:, inject:into:, detect:, detect:ifNone:, anySatisfy: and allSatisfy:
 */
public final class RandomAccessListIterate
{
    private RandomAccessListIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    public static <T> void toArray(List<T> list, T[] target, int startIndex, int sourceSize)
    {
        for (int i = 0; i < sourceSize; i++)
        {
            target[startIndex + i] = list.get(i);
        }
    }

    /**
     * @see Iterate#select(Iterable, Predicate)
     */
    public static <T> MutableList<T> select(List<T> list, Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.select(list, predicate, FastList.<T>newList());
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> MutableList<T> selectWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        return RandomAccessListIterate.selectWith(list, predicate, injectedValue, FastList.<T>newList());
    }

    /**
     * @see Iterate#select(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R select(
            List<T> list,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            if (predicate.accept(item))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#selectWith(Iterable, Predicate2, Object, Collection)
     */
    public static <T, P, R extends Collection<T>> R selectWith(
            List<T> list,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            if (predicate.accept(item, parameter))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#selectInstancesOf(Iterable, Class)
     */
    public static <T> MutableList<T> selectInstancesOf(
            List<?> list,
            Class<T> clazz)
    {
        int size = list.size();
        FastList<T> result = FastList.newList(size);

        for (int i = 0; i < size; i++)
        {
            Object item = list.get(i);
            if (clazz.isInstance(item))
            {
                result.add((T) item);
            }
        }
        result.trimToSize();
        return result;
    }

    /**
     * @see Iterate#count(Iterable, Predicate)
     */
    public static <T> int count(
            List<T> list,
            Predicate<? super T> predicate)
    {
        int count = 0;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(list.get(i)))
            {
                count++;
            }
        }
        return count;
    }

    public static <T, IV> int countWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        int count = 0;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(list.get(i), injectedValue))
            {
                count++;
            }
        }
        return count;
    }

    /**
     * @see Iterate#collectIf(Iterable, Predicate, Function)
     */
    public static <T, A> MutableList<A> collectIf(
            List<T> list,
            Predicate<? super T> predicate,
            Function<? super T, ? extends A> function)
    {
        return RandomAccessListIterate.collectIf(list, predicate, function, FastList.<A>newList());
    }

    /**
     * @see Iterate#collectIf(Iterable, Predicate, Function, Collection)
     */
    public static <T, A, R extends Collection<A>> R collectIf(
            List<T> list,
            Predicate<? super T> predicate,
            Function<? super T, ? extends A> function,
            R targetCollection)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            if (predicate.accept(item))
            {
                targetCollection.add(function.valueOf(item));
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#reject(Iterable, Predicate)
     */
    public static <T> MutableList<T> reject(List<T> list, Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.reject(list, predicate, FastList.<T>newList());
    }

    /**
     * @see Iterate#rejectWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> MutableList<T> rejectWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        return RandomAccessListIterate.rejectWith(list, predicate, injectedValue, FastList.<T>newList());
    }

    /**
     * @see Iterate#reject(Iterable, Predicate, Collection)
     */
    public static <T, R extends Collection<T>> R reject(
            List<T> list,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            if (!predicate.accept(item))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#reject(Iterable, Predicate, Collection)
     */
    public static <T, P, R extends Collection<T>> R rejectWith(
            List<T> list,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            if (!predicate.accept(item, parameter))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#collect(Iterable, Function)
     */
    public static <T, A> MutableList<A> collect(
            List<T> list,
            Function<? super T, ? extends A> function)
    {
        return collect(list, function, FastList.<A>newList(list.size()));
    }

    /**
     * @see Iterate#collect(Iterable, Function, Collection)
     */
    public static <T, A, R extends Collection<A>> R collect(
            List<T> list,
            Function<? super T, ? extends A> function,
            R targetCollection)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            targetCollection.add(function.valueOf(list.get(i)));
        }
        return targetCollection;
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction)
     */
    public static <T> MutableBooleanList collectBoolean(
            List<T> list,
            BooleanFunction<? super T> booleanFunction)
    {
        return RandomAccessListIterate.collectBoolean(list, booleanFunction, new BooleanArrayList(list.size()));
    }

    /**
     * @see Iterate#collectBoolean(Iterable, BooleanFunction)
     */
    public static <T, R extends MutableBooleanCollection> R collectBoolean(
            List<T> list,
            BooleanFunction<? super T> booleanFunction,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(booleanFunction.booleanValueOf(list.get(i)));
        }
        return target;
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction)
     */
    public static <T> MutableByteList collectByte(
            List<T> list,
            ByteFunction<? super T> byteFunction)
    {
        return RandomAccessListIterate.collectByte(list, byteFunction, new ByteArrayList(list.size()));
    }

    /**
     * @see Iterate#collectByte(Iterable, ByteFunction)
     */
    public static <T, R extends MutableByteCollection> R collectByte(
            List<T> list,
            ByteFunction<? super T> byteFunction,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(byteFunction.byteValueOf(list.get(i)));
        }
        return target;
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction)
     */
    public static <T> MutableCharList collectChar(
            List<T> list,
            CharFunction<? super T> charFunction)
    {
        return RandomAccessListIterate.collectChar(list, charFunction, new CharArrayList(list.size()));
    }

    /**
     * @see Iterate#collectChar(Iterable, CharFunction)
     */
    public static <T, R extends MutableCharCollection> R collectChar(
            List<T> list,
            CharFunction<? super T> charFunction,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(charFunction.charValueOf(list.get(i)));
        }
        return target;
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction)
     */
    public static <T> MutableDoubleList collectDouble(
            List<T> list,
            DoubleFunction<? super T> doubleFunction)
    {
        return RandomAccessListIterate.collectDouble(list, doubleFunction, new DoubleArrayList(list.size()));
    }

    /**
     * @see Iterate#collectDouble(Iterable, DoubleFunction)
     */
    public static <T, R extends MutableDoubleCollection> R collectDouble(
            List<T> list,
            DoubleFunction<? super T> doubleFunction,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(doubleFunction.doubleValueOf(list.get(i)));
        }
        return target;
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction)
     */
    public static <T> MutableFloatList collectFloat(
            List<T> list,
            FloatFunction<? super T> floatFunction)
    {
        return RandomAccessListIterate.collectFloat(list, floatFunction, new FloatArrayList(list.size()));
    }

    /**
     * @see Iterate#collectFloat(Iterable, FloatFunction)
     */
    public static <T, R extends MutableFloatCollection> R collectFloat(
            List<T> list,
            FloatFunction<? super T> floatFunction,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(floatFunction.floatValueOf(list.get(i)));
        }
        return target;
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction)
     */
    public static <T> MutableIntList collectInt(
            List<T> list,
            IntFunction<? super T> intFunction)
    {
        return RandomAccessListIterate.collectInt(list, intFunction, new IntArrayList(list.size()));
    }

    /**
     * @see Iterate#collectInt(Iterable, IntFunction)
     */
    public static <T, R extends MutableIntCollection> R collectInt(
            List<T> list,
            IntFunction<? super T> intFunction,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(intFunction.intValueOf(list.get(i)));
        }
        return target;
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction)
     */
    public static <T> MutableLongList collectLong(
            List<T> list,
            LongFunction<? super T> longFunction)
    {
        return RandomAccessListIterate.collectLong(list, longFunction, new LongArrayList(list.size()));
    }

    /**
     * @see Iterate#collectLong(Iterable, LongFunction)
     */
    public static <T, R extends MutableLongCollection> R collectLong(
            List<T> list,
            LongFunction<? super T> longFunction,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(longFunction.longValueOf(list.get(i)));
        }
        return target;
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction)
     */
    public static <T> MutableShortList collectShort(
            List<T> list,
            ShortFunction<? super T> shortFunction)
    {
        return RandomAccessListIterate.collectShort(list, shortFunction, new ShortArrayList(list.size()));
    }

    /**
     * @see Iterate#collectShort(Iterable, ShortFunction)
     */
    public static <T, R extends MutableShortCollection> R collectShort(
            List<T> list,
            ShortFunction<? super T> shortFunction,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(shortFunction.shortValueOf(list.get(i)));
        }
        return target;
    }

    /**
     * @see Iterate#flatCollect(Iterable, Function)
     */
    public static <T, A> MutableList<A> flatCollect(
            List<T> list,
            Function<? super T, ? extends Iterable<A>> function)
    {
        return flatCollect(list, function, FastList.<A>newList(list.size()));
    }

    /**
     * @see Iterate#flatCollect(Iterable, Function, Collection)
     */
    public static <T, A, R extends Collection<A>> R flatCollect(
            List<T> list,
            Function<? super T, ? extends Iterable<A>> function,
            R targetCollection)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            Iterate.addAllTo(function.valueOf(list.get(i)), targetCollection);
        }
        return targetCollection;
    }

    /**
     * Returns the last element of a list.
     */
    public static <T> T getLast(List<T> collection)
    {
        return Iterate.isEmpty(collection) ? null : collection.get(collection.size() - 1);
    }

    /**
     * @see Iterate#forEach(Iterable, Procedure)
     */
    public static <T> void forEach(List<T> list, Procedure<? super T> procedure)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            procedure.value(list.get(i));
        }
    }

    /**
     * Iterates over the section of the list covered by the specified indexes.  The indexes are both inclusive.  If the
     * from is less than the to, the list is iterated in forward order. If the from is greater than the to, then the
     * list is iterated in the reverse order.
     * <p>
     * <pre>e.g.
     * MutableList<People> people = FastList.newListWith(ted, mary, bob, sally);
     * ListIterate.forEach(people, 0, 1, new Procedure<Person>()
     * {
     *     public void value(Person person)
     *     {
     *          LOGGER.info(person.getName());
     *     }
     * });
     * </pre>
     * <p>
     * This code would output ted and mary's names.
     */
    public static <T> void forEach(List<T> list, int from, int to, Procedure<? super T> procedure)
    {
        ListIterate.rangeCheck(from, to, list.size());
        if (from <= to)
        {
            for (int i = from; i <= to; i++)
            {
                procedure.value(list.get(i));
            }
        }
        else
        {
            for (int i = from; i >= to; i--)
            {
                procedure.value(list.get(i));
            }
        }
    }

    /**
     * Iterates over the section of the list covered by the specified indexes.  The indexes are both inclusive.  If the
     * from is less than the to, the list is iterated in forward order. If the from is greater than the to, then the
     * list is iterated in the reverse order. The index passed into the ObjectIntProcedure is the actual index of the
     * range.
     * <p>
     * <pre>e.g.
     * MutableList<People> people = FastList.newListWith(ted, mary, bob, sally);
     * ListIterate.forEachWithIndex(people, 0, 1, new ObjectIntProcedure<Person>()
     * {
     *     public void value(Person person, int index)
     *     {
     *          LOGGER.info(person.getName() + " at index: " + index);
     *     }
     * });
     * </pre>
     * <p>
     * This code would output ted and mary's names.
     */
    public static <T> void forEachWithIndex(List<T> list, int from, int to, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ListIterate.rangeCheck(from, to, list.size());
        if (from <= to)
        {
            for (int i = from; i <= to; i++)
            {
                objectIntProcedure.value(list.get(i), i);
            }
        }
        else
        {
            for (int i = from; i >= to; i--)
            {
                objectIntProcedure.value(list.get(i), i);
            }
        }
    }

    /**
     * For each element in both of the Lists, operation is evaluated with both elements as parameters.
     */
    public static <T1, T2> void forEachInBoth(List<T1> list1, List<T2> list2, Procedure2<? super T1, ? super T2> procedure)
    {
        if (list1 != null && list2 != null)
        {
            int size1 = list1.size();
            int size2 = list2.size();
            if (size1 == size2)
            {
                for (int i = 0; i < size1; i++)
                {
                    procedure.value(list1.get(i), list2.get(i));
                }
            }
            else
            {
                throw new IllegalArgumentException("Attempt to call forEachInBoth with two Lists of different sizes :"
                        + size1
                        + ':'
                        + size2);
            }
        }
    }

    /**
     * Iterates over a collection passing each element and the current relative int index to the specified instance of
     * ObjectIntProcedure.
     */
    public static <T> void forEachWithIndex(List<T> list, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            objectIntProcedure.value(list.get(i), i);
        }
    }

    public static <T, IV> IV injectInto(IV injectValue, List<T> list, Function2<? super IV, ? super T, ? extends IV> function)
    {
        IV result = injectValue;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            result = function.value(result, list.get(i));
        }
        return result;
    }

    public static <T> int injectInto(int injectValue, List<T> list, IntObjectToIntFunction<? super T> function)
    {
        int result = injectValue;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            result = function.intValueOf(result, list.get(i));
        }
        return result;
    }

    public static <T> long injectInto(long injectValue, List<T> list, LongObjectToLongFunction<? super T> function)
    {
        long result = injectValue;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            result = function.longValueOf(result, list.get(i));
        }
        return result;
    }

    public static <T> double injectInto(double injectValue, List<T> list, DoubleObjectToDoubleFunction<? super T> function)
    {
        double result = injectValue;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            result = function.doubleValueOf(result, list.get(i));
        }
        return result;
    }

    public static <T> float injectInto(float injectValue, List<T> list, FloatObjectToFloatFunction<? super T> function)
    {
        float result = injectValue;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            result = function.floatValueOf(result, list.get(i));
        }
        return result;
    }

    public static <T> long sumOfInt(List<T> list, IntFunction<? super T> function)
    {
        long result = 0;
        for (int i = 0; i < list.size(); i++)
        {
            result += (long) function.intValueOf(list.get(i));
        }
        return result;
    }

    public static <T> long sumOfLong(List<T> list, LongFunction<? super T> function)
    {
        long result = 0L;
        for (int i = 0; i < list.size(); i++)
        {
            result += function.longValueOf(list.get(i));
        }
        return result;
    }

    public static <T> double sumOfFloat(List<T> list, FloatFunction<? super T> function)
    {
        double sum = 0.0d;
        double compensation = 0.0d;
        for (int i = 0; i < list.size(); i++)
        {
            double adjustedValue = (double) function.floatValueOf(list.get(i)) - compensation;
            double nextSum = sum + adjustedValue;
            compensation = nextSum - sum - adjustedValue;
            sum = nextSum;
        }
        return sum;
    }

    public static <T> double sumOfDouble(List<T> list, DoubleFunction<? super T> function)
    {
        double sum = 0.0d;
        double compensation = 0.0d;
        for (int i = 0; i < list.size(); i++)
        {
            double adjustedValue = function.doubleValueOf(list.get(i)) - compensation;
            double nextSum = sum + adjustedValue;
            compensation = nextSum - sum - adjustedValue;
            sum = nextSum;
        }
        return sum;
    }

    public static <T> BigDecimal sumOfBigDecimal(List<T> list, Function<? super T, BigDecimal> function)
    {
        BigDecimal result = BigDecimal.ZERO;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            result = result.add(function.valueOf(list.get(i)));
        }
        return result;
    }

    public static <T> BigInteger sumOfBigInteger(List<T> list, Function<? super T, BigInteger> function)
    {
        BigInteger result = BigInteger.ZERO;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            result = result.add(function.valueOf(list.get(i)));
        }
        return result;
    }

    public static <V, T> MutableMap<V, BigDecimal> sumByBigDecimal(List<T> list, Function<T, V> groupBy, final Function<? super T, BigDecimal> function)
    {
        MutableMap<V, BigDecimal> result = UnifiedMap.newMap();
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            final T item = list.get(i);
            result.updateValue(groupBy.valueOf(item), Functions0.zeroBigDecimal(), new Function<BigDecimal, BigDecimal>()
            {
                public BigDecimal valueOf(BigDecimal original)
                {
                    return original.add(function.valueOf(item));
                }
            });
        }
        return result;
    }

    public static <V, T> MutableMap<V, BigInteger> sumByBigInteger(List<T> list, Function<T, V> groupBy, final Function<? super T, BigInteger> function)
    {
        MutableMap<V, BigInteger> result = UnifiedMap.newMap();
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            final T item = list.get(i);
            result.updateValue(groupBy.valueOf(item), Functions0.zeroBigInteger(), new Function<BigInteger, BigInteger>()
            {
                public BigInteger valueOf(BigInteger original)
                {
                    return original.add(function.valueOf(item));
                }
            });
        }
        return result;
    }

    public static <T, V> V shortCircuit(
            List<T> list,
            Predicate<? super T> predicate,
            boolean expected,
            Function<? super T, ? extends V> onShortCircuit,
            Function0<? extends V> atEnd)
    {
        for (int i = 0; i < list.size(); i++)
        {
            T each = list.get(i);
            if (predicate.accept(each) == expected)
            {
                return onShortCircuit.valueOf(each);
            }
        }
        return atEnd.value();
    }

    public static <T, P, V> V shortCircuitWith(
            List<T> list,
            Predicate2<? super T, ? super P> predicate2,
            P parameter,
            boolean expected,
            Function<? super T, ? extends V> onShortCircuit,
            Function0<? extends V> atEnd)
    {
        for (int i = 0; i < list.size(); i++)
        {
            T each = list.get(i);
            if (predicate2.accept(each, parameter) == expected)
            {
                return onShortCircuit.valueOf(each);
            }
        }
        return atEnd.value();
    }

    public static <T> boolean anySatisfy(List<T> list, Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.shortCircuit(list, predicate, true, Functions.getTrue(), Functions0.getFalse());
    }

    public static <T, P> boolean anySatisfyWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return RandomAccessListIterate.shortCircuitWith(list, predicate, parameter, true, Functions.getTrue(), Functions0.getFalse());
    }

    public static <T> boolean allSatisfy(List<T> list, Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.shortCircuit(list, predicate, false, Functions.getFalse(), Functions0.getTrue());
    }

    public static <T, P> boolean allSatisfyWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return RandomAccessListIterate.shortCircuitWith(list, predicate, parameter, false, Functions.getFalse(), Functions0.getTrue());
    }

    public static <T> boolean noneSatisfy(List<T> list, Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.shortCircuit(list, predicate, true, Functions.getFalse(), Functions0.getTrue());
    }

    public static <T, P> boolean noneSatisfyWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return RandomAccessListIterate.shortCircuitWith(list, predicate, parameter, true, Functions.getFalse(), Functions0.getTrue());
    }

    public static <T> T detect(List<T> list, Predicate<? super T> predicate)
    {
        return RandomAccessListIterate.shortCircuit(list, predicate, true, Functions.<T>identity(), Functions0.<T>nullValue());
    }

    public static <T, P> T detectWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return RandomAccessListIterate.shortCircuitWith(list, predicate, parameter, true, Functions.<T>identity(), Functions0.<T>nullValue());
    }

    public static <T, IV> Twin<MutableList<T>> selectAndRejectWith(
            List<T> list,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        MutableList<T> positiveResult = Lists.mutable.of();
        MutableList<T> negativeResult = Lists.mutable.of();
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            (predicate.accept(item, injectedValue) ? positiveResult : negativeResult).add(item);
        }
        return Tuples.twin(positiveResult, negativeResult);
    }

    public static <T> PartitionMutableList<T> partition(List<T> list, Predicate<? super T> predicate)
    {
        PartitionFastList<T> partitionFastList = new PartitionFastList<T>();

        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T each = list.get(i);
            MutableList<T> bucket = predicate.accept(each) ? partitionFastList.getSelected() : partitionFastList.getRejected();
            bucket.add(each);
        }
        return partitionFastList;
    }

    public static <T, P> PartitionMutableList<T> partitionWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionFastList<T> partitionFastList = new PartitionFastList<T>();

        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T each = list.get(i);
            MutableList<T> bucket = predicate.accept(each, parameter) ? partitionFastList.getSelected() : partitionFastList.getRejected();
            bucket.add(each);
        }
        return partitionFastList;
    }

    public static <T> List<T> removeIf(List<T> list, Predicate<? super T> predicate)
    {
        for (int i = 0; i < list.size(); i++)
        {
            T each = list.get(i);
            if (predicate.accept(each))
            {
                list.remove(i--);
            }
        }
        return list;
    }

    public static <T, P> List<T> removeIfWith(List<T> list, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        for (int i = 0; i < list.size(); i++)
        {
            T each = list.get(i);
            if (predicate.accept(each, parameter))
            {
                list.remove(i--);
            }
        }
        return list;
    }

    public static <T> List<T> removeIf(List<T> list, Predicate<? super T> predicate, Procedure<? super T> procedure)
    {
        for (int i = 0; i < list.size(); i++)
        {
            T each = list.get(i);
            if (predicate.accept(each))
            {
                procedure.value(each);
                list.remove(i--);
            }
        }
        return list;
    }

    /**
     * Searches for the first occurrence where the predicate evaluates to true.
     *
     * @see Iterate#detectIndex(Iterable, Predicate)
     */
    public static <T> int detectIndex(List<T> list, Predicate<? super T> predicate)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(list.get(i)))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Searches for the first occurrence where the predicate evaluates to true.
     *
     * @see Iterate#detectIndexWith(Iterable, Predicate2, Object)
     */
    public static <T, IV> int detectIndexWith(List<T> list, Predicate2<? super T, ? super IV> predicate, IV injectedValue)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(list.get(i), injectedValue))
            {
                return i;
            }
        }
        return -1;
    }

    public static <T, IV, P> IV injectIntoWith(
            IV injectedValue,
            List<T> list,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        IV result = injectedValue;
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            result = function.value(result, list.get(i), parameter);
        }
        return result;
    }

    public static <T, P> void forEachWith(List<T> list, Procedure2<? super T, ? super P> procedure, P parameter)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            procedure.value(list.get(i), parameter);
        }
    }

    public static <T, P, A, R extends Collection<A>> R collectWith(
            List<T> list,
            Function2<? super T, ? super P, ? extends A> function,
            P parameter,
            R targetCollection)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            targetCollection.add(function.value(list.get(i), parameter));
        }
        return targetCollection;
    }

    public static <T, R extends Collection<T>> R distinct(List<T> list, R targetCollection)
    {
        MutableSet<T> seenSoFar = UnifiedSet.newSet();
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            if (seenSoFar.add(item))
            {
                targetCollection.add(item);
            }
        }
        return targetCollection;
    }

    /**
     * @see Iterate#take(Iterable, int)
     */
    public static <T> MutableList<T> take(List<T> list, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return RandomAccessListIterate.take(list, count, FastList.<T>newList(Math.min(list.size(), count)));
    }

    /**
     * @see Iterate#take(Iterable, int)
     */
    public static <T, R extends Collection<T>> R take(List<T> list, int count, R targetList)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        int end = Math.min(list.size(), count);
        for (int i = 0; i < end; i++)
        {
            targetList.add(list.get(i));
        }
        return targetList;
    }

    /**
     * @see Iterate#drop(Iterable, int)
     */
    public static <T> MutableList<T> drop(List<T> list, int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        return RandomAccessListIterate.drop(list, count, FastList.<T>newList(list.size() - Math.min(list.size(), count)));
    }

    /**
     * @see Iterate#drop(Iterable, int)
     */
    public static <T, R extends Collection<T>> R drop(List<T> list, int count, R targetList)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }
        if (count >= list.size())
        {
            return targetList;
        }
        int start = Math.min(list.size(), count);
        targetList.addAll(list.subList(start, list.size()));
        return targetList;
    }

    /**
     * @see RichIterable#appendString(Appendable, String, String, String)
     */
    public static <T> void appendString(
            List<T> list,
            Appendable appendable,
            String start,
            String separator,
            String end)
    {
        try
        {
            appendable.append(start);

            if (Iterate.notEmpty(list))
            {
                appendable.append(IterableIterate.stringValueOfItem(list, list.get(0)));

                int size = list.size();
                for (int i = 1; i < size; i++)
                {
                    appendable.append(separator);
                    appendable.append(IterableIterate.stringValueOfItem(list, list.get(i)));
                }
            }

            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see Iterate#groupBy(Iterable, Function)
     */
    public static <T, V> FastListMultimap<V, T> groupBy(
            List<T> list,
            Function<? super T, ? extends V> function)
    {
        return RandomAccessListIterate.groupBy(list, function, FastListMultimap.<V, T>newMultimap());
    }

    /**
     * @see Iterate#groupBy(Iterable, Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupBy(
            List<T> list,
            Function<? super T, ? extends V> function,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            target.put(function.valueOf(item), item);
        }
        return target;
    }

    public static <T, V> FastListMultimap<V, T> groupByEach(
            List<T> list,
            Function<? super T, ? extends Iterable<V>> function)
    {
        return RandomAccessListIterate.groupByEach(list, function, FastListMultimap.<V, T>newMultimap());
    }

    public static <T, V, R extends MutableMultimap<V, T>> R groupByEach(
            List<T> list,
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T item = list.get(i);
            Iterable<V> iterable = function.valueOf(item);
            for (V key : iterable)
            {
                target.put(key, item);
            }
        }
        return target;
    }

    public static <K, T> MutableMap<K, T> groupByUniqueKey(
            List<T> list,
            Function<? super T, ? extends K> function)
    {
        return RandomAccessListIterate.groupByUniqueKey(list, function, UnifiedMap.<K, T>newMap());
    }

    public static <K, T, R extends MutableMap<K, T>> R groupByUniqueKey(
            List<T> list,
            Function<? super T, ? extends K> function,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T value = list.get(i);
            K key = function.valueOf(value);
            if (target.put(key, value) != null)
            {
                throw new IllegalStateException("Key " + key + " already exists in map!");
            }
        }
        return target;
    }

    public static <T, V extends Comparable<? super V>> T minBy(List<T> list, Function<? super T, ? extends V> function)
    {
        if (list.isEmpty())
        {
            throw new NoSuchElementException();
        }

        T min = list.get(0);
        V minValue = function.valueOf(min);
        int size = list.size();
        for (int i = 1; i < size; i++)
        {
            T next = list.get(i);
            V nextValue = function.valueOf(next);
            if (nextValue.compareTo(minValue) < 0)
            {
                min = next;
                minValue = nextValue;
            }
        }
        return min;
    }

    public static <T, V extends Comparable<? super V>> T maxBy(List<T> list, Function<? super T, ? extends V> function)
    {
        if (list.isEmpty())
        {
            throw new NoSuchElementException();
        }

        T max = list.get(0);
        V maxValue = function.valueOf(max);
        int size = list.size();
        for (int i = 1; i < size; i++)
        {
            T next = list.get(i);
            V nextValue = function.valueOf(next);
            if (nextValue.compareTo(maxValue) > 0)
            {
                max = next;
                maxValue = nextValue;
            }
        }
        return max;
    }

    public static <T> T min(List<T> list, Comparator<? super T> comparator)
    {
        if (list.isEmpty())
        {
            throw new NoSuchElementException();
        }

        T min = list.get(0);
        int size = list.size();
        for (int i = 1; i < size; i++)
        {
            T item = list.get(i);
            if (comparator.compare(item, min) < 0)
            {
                min = item;
            }
        }
        return min;
    }

    public static <T> T max(List<T> list, Comparator<? super T> comparator)
    {
        if (list.isEmpty())
        {
            throw new NoSuchElementException();
        }

        T max = list.get(0);
        int size = list.size();
        for (int i = 1; i < size; i++)
        {
            T item = list.get(i);
            if (comparator.compare(item, max) > 0)
            {
                max = item;
            }
        }
        return max;
    }

    public static <T> T min(List<T> list)
    {
        if (list.isEmpty())
        {
            throw new NoSuchElementException();
        }

        T min = list.get(0);
        int size = list.size();
        for (int i = 1; i < size; i++)
        {
            T item = list.get(i);
            if (((Comparable<? super T>) item).compareTo(min) < 0)
            {
                min = item;
            }
        }
        return min;
    }

    public static <T> T max(List<T> list)
    {
        if (list.isEmpty())
        {
            throw new NoSuchElementException();
        }

        T max = list.get(0);
        int size = list.size();
        for (int i = 1; i < size; i++)
        {
            T item = list.get(i);
            if (((Comparable<T>) item).compareTo(max) > 0)
            {
                max = item;
            }
        }
        return max;
    }

    public static <X, Y> MutableList<Pair<X, Y>> zip(
            List<X> list,
            Iterable<Y> iterable)
    {
        return RandomAccessListIterate.zip(list, iterable, FastList.<Pair<X, Y>>newList());
    }

    public static <X, Y, R extends Collection<Pair<X, Y>>> R zip(
            List<X> list,
            Iterable<Y> iterable,
            R target)
    {
        Iterator<Y> yIterator = iterable.iterator();
        int size = list.size();
        for (int i = 0; i < size && yIterator.hasNext(); i++)
        {
            target.add(Tuples.pair(list.get(i), yIterator.next()));
        }
        return target;
    }

    public static <T> MutableList<Pair<T, Integer>> zipWithIndex(List<T> list)
    {
        return RandomAccessListIterate.zipWithIndex(list, FastList.<Pair<T, Integer>>newList());
    }

    public static <T, R extends Collection<Pair<T, Integer>>> R zipWithIndex(
            List<T> list,
            R target)
    {
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            target.add(Tuples.pair(list.get(i), i));
        }
        return target;
    }

    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            List<T> list,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        MutableMap<K, V> map = UnifiedMap.newMap();
        RandomAccessListIterate.forEach(list, new MutatingAggregationProcedure<T, K, V>(map, groupBy, zeroValueFactory, mutatingAggregator));
        return map;
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            List<T> list,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        MutableMap<K, V> map = UnifiedMap.newMap();
        RandomAccessListIterate.forEach(list, new NonMutatingAggregationProcedure<T, K, V>(map, groupBy, zeroValueFactory, nonMutatingAggregator));
        return map;
    }

    public static <T> MutableList<T> takeWhile(List<T> list, Predicate<? super T> predicate)
    {
        MutableList<T> result = FastList.newList();
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T each = list.get(i);
            if (predicate.accept(each))
            {
                result.add(each);
            }
            else
            {
                return result;
            }
        }
        return result;
    }

    public static <T> MutableList<T> dropWhile(List<T> list, Predicate<? super T> predicate)
    {
        MutableList<T> result = FastList.newList();
        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T each = list.get(i);
            if (!predicate.accept(each))
            {
                result.add(each);
                for (int j = i + 1; j < size; j++)
                {
                    T eachNotDropped = list.get(j);
                    result.add(eachNotDropped);
                }
                return result;
            }
        }
        return result;
    }

    public static <T> PartitionMutableList<T> partitionWhile(List<T> list, Predicate<? super T> predicate)
    {
        PartitionMutableList<T> result = new PartitionFastList<T>();
        MutableList<T> selected = result.getSelected();

        int size = list.size();
        for (int i = 0; i < size; i++)
        {
            T each = list.get(i);
            if (predicate.accept(each))
            {
                selected.add(each);
            }
            else
            {
                MutableList<T> rejected = result.getRejected();
                rejected.add(each);
                for (int j = i + 1; j < size; j++)
                {
                    T eachRejected = list.get(j);
                    rejected.add(eachRejected);
                }
                return result;
            }
        }
        return result;
    }

    public static <V, T> ObjectLongMap<V> sumByInt(List<T> list, Function<T, V> groupBy, IntFunction<? super T> function)
    {
        ObjectLongHashMap<V> result = ObjectLongHashMap.newMap();
        for (int i = 0; i < list.size(); i++)
        {
            T item = list.get(i);
            result.addToValue(groupBy.valueOf(item), function.intValueOf(item));
        }
        return result;
    }

    public static <V, T> ObjectLongMap<V> sumByLong(List<T> list, Function<T, V> groupBy, LongFunction<? super T> function)
    {
        ObjectLongHashMap<V> result = ObjectLongHashMap.newMap();
        for (int i = 0; i < list.size(); i++)
        {
            T item = list.get(i);
            result.addToValue(groupBy.valueOf(item), function.longValueOf(item));
        }
        return result;
    }

    public static <V, T> ObjectDoubleMap<V> sumByFloat(List<T> list, Function<T, V> groupBy, FloatFunction<? super T> function)
    {
        ObjectDoubleHashMap<V> result = ObjectDoubleHashMap.newMap();
        for (int i = 0; i < list.size(); i++)
        {
            T item = list.get(i);
            result.addToValue(groupBy.valueOf(item), function.floatValueOf(item));
        }
        return result;
    }

    public static <V, T> ObjectDoubleMap<V> sumByDouble(List<T> list, Function<T, V> groupBy, DoubleFunction<? super T> function)
    {
        ObjectDoubleHashMap<V> result = ObjectDoubleHashMap.newMap();
        for (int i = 0; i < list.size(); i++)
        {
            T item = list.get(i);
            result.addToValue(groupBy.valueOf(item), function.doubleValueOf(item));
        }
        return result;
    }
}

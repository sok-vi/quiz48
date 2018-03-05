/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48;

/**
 *
 * @author vasya
 * @param <T>
 */
public final class Pointer <T> {
    private T m_Pointer = null;
    public Pointer(T p) { m_Pointer = p; }
    public Pointer() { }
    public final T get() { return m_Pointer; }
    public final void put(T p) { m_Pointer = p; }
}

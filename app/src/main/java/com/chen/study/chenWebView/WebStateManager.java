package com.chen.study.chenWebView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PengChen on 2016/9/20.
 */
public class WebStateManager {

    public static final Input Empty = new Input();

    public interface IWebStateOwner {
        WebStateManager manager();

        WebState current();

        void gotoState(WebState webState);
    }

    public class WebStateOwner implements IWebStateOwner {
        private AtomicReference<WebState> _state = new AtomicReference<WebState>();

        @Override
        public WebState current() {
            return _state.get();
        }

        @Override
        public void gotoState(WebState webState) {
            _state.getAndSet(webState);
        }

        @Override
        public WebStateManager manager() {
            return WebStateManager.this;
        }
    }

    public static class Input {
        private int _type = 0;

        public int getType() {
            return _type;
        }

        public void setType(int type) {
            this._type = type;
        }

        private Object _arg;

        public Object getArg() {
            return _arg;
        }

        public void setArg(Object _arg) {
            this._arg = _arg;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Input)) return false;
            Input input = (Input) o;
            return _type == input._type;
        }

        @Override
        public int hashCode() {
            return _type;
        }
    }

    public static abstract class WebState {
        public abstract void exec(IWebStateOwner owner, Input input);

        protected abstract void init(IWebStateOwner owner, Input preInput);

        protected abstract void release(IWebStateOwner owner);
    }

    public static abstract class WebStateBase extends WebState {
        @Override
        public void exec(IWebStateOwner owner, Input input) {
            String state = judge(input);
            if (state != null) {
                WebState webState = owner.manager().get(state);
                if (state != null) {
                    owner.current().release(owner);
//                    Logger.e("goto " + state + " input " + StateParams.getTypeName(input.getType()));
                    owner.gotoState(webState);
                    webState.init(owner, input);
                }
            }
        }

        protected void init(IWebStateOwner owner, Input preInput) {

        }

        protected void release(IWebStateOwner owner) {

        }

        protected abstract String judge(Input input);
    }

    private IWebStateOwner _owner;
    private Map<String, WebState> _stateMap = new HashMap<String, WebState>();

    public WebStateManager(IWebStateOwner owner) {
        _owner = owner;
    }

    public WebStateManager() {
        _owner = new WebStateOwner();
    }

    public void setInitState(String name) {
        WebState state = get(name);
        if (state != null) {
            _owner.gotoState(state);
            state.init(_owner, Empty);
        }
    }

    public void input(Input input) {
        _owner.current().exec(_owner, input);
    }

    public void input(int type) {
        Input input = new Input();
        input.setType(type);
        _owner.current().exec(_owner, input);
    }

    public void define(String name, WebState state) {
        _stateMap.put(name, state);
    }

    public WebState get(String name) {
        return _stateMap.get(name);
    }
}

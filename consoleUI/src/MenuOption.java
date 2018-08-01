public enum MenuOption {
    LOAD_XML {
        @Override
        public String toString() {
            return "1)Load XML File";
        }
    },
    START {
        @Override
        public String toString() {
            return "2)Start Game";
        }
    },
    SETTINGS {
        @Override
        public String toString() {
            return "3)Show Game Settings";
        }
    },
    MAKE_MOVE{
        @Override
        public String toString() {
            return "4)Make Move";
        }
    },
    HISTORY{
        @Override
        public String toString() {
            return "5)Show Turn History";
        }
    },
    UNDO {
        @Override
        public String toString() {
            return "6)Undo Last Turn";
        }
    },
    SAVE {
        @Override
        public String toString() {
            return "7)Save Game";
        }
    },
    LOAD{
        @Override
        public String toString() {
            return "8)Load Game";
        }
    },
    EXIT {
        @Override
        public String toString() {
            return "9)Exit Game";
        }
    };
}

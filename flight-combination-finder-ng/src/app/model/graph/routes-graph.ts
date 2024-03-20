import { GraphCategory } from "./graph-category";
import { GraphLink } from "./graph-link";
import { GraphNode } from "./graph-node";

export interface RoutesGraph {
    nodes?: GraphNode[];
    links?: GraphLink[];
    categories?: GraphCategory[];
}